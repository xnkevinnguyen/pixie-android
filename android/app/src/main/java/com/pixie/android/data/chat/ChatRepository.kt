package com.pixie.android.data.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatRepository(
    private val dataSource: ChatDataSource,
    private val userRepository: UserRepository
) {


    val MAIN_CHANNEL_ID = 3.0
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf())
    }
    private var mainChannelParticipantList =
        MutableLiveData<MutableList<ChannelParticipant>>().apply {
            val loadingUsername = "Loading ..."
            val loadingID = 0.0
            this.postValue(arrayListOf(ChannelParticipant(loadingID, loadingUsername, false)))
        }
    private var userChannels = MutableLiveData<ArrayList<ChannelData>>()

    private lateinit var mainChannelMessageJob: Job
    private lateinit var mainChannelParticipantJob: Job


    fun getUserChannels(): LiveData<ArrayList<ChannelData>> {
        return userChannels
    }

    fun getMainChannelMessageList(): LiveData<MutableList<MessageData>> {
        return mainChannelMessageList
    }

    fun getMainChannelParticipantList(): LiveData<MutableList<ChannelParticipant>> {
        return mainChannelParticipantList
    }

    fun fetchUserChannels() {
        CoroutineScope(IO).launch {
            val channelDataList = dataSource.getUserChannels(userRepository.getUser().userId)
            userChannels.postValue(channelDataList?.toMutableList())
        }
    }

    fun enterMainChannel() {
        CoroutineScope(IO).launch {

            val channelData =
                dataSource.enterChannel(MAIN_CHANNEL_ID, userRepository.getUser().userId)
            if (channelData != null)
                mainChannelParticipantList.postValue(channelData.participantList.toMutableList())


        }
    }

    fun clearChannels() {
        mainChannelMessageList.postValue(arrayListOf())
        mainChannelParticipantList.postValue(arrayListOf())
    }

    fun exitMainChannel() {
        CoroutineScope(IO).launch {
            val userID = userRepository.getUser().userId
            dataSource.exitChannel(MAIN_CHANNEL_ID, userID)

        }
    }

    fun cancelMainChannelSubscriptions() {
        mainChannelMessageJob.cancel()
        mainChannelParticipantJob.cancel()
    }

    fun subscribeChannelMessages() {
        mainChannelMessageJob = CoroutineScope(IO).launch {
            dataSource.suscribeToChannelMessages(MAIN_CHANNEL_ID, onReceiveMessage = {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    if (it.userName != userRepository.getUser().username) {
                        mainChannelMessageList.value?.add(it)
                        mainChannelMessageList.notifyObserver()
                    }
                }

            })
        }
    }

    fun suscribeChannelUsers() {
        mainChannelParticipantJob = CoroutineScope(IO).launch {
            dataSource.suscribeToChannelChange(MAIN_CHANNEL_ID, onChannelChange = {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    mainChannelParticipantList.postValue(it.participantList.toMutableList())
                }

            })
        }
    }


    fun sendMessage(message: String) {

        val messageData =
            MessageData(message, true, timePosted = Calendar.getInstance().timeInMillis.toString())
        mainChannelMessageList.value?.add(messageData)
        CoroutineScope(IO).launch {
            val data = dataSource.sendMessageToChannel(
                messageData,
                MAIN_CHANNEL_ID,
                userRepository.getUser().userId
            )
            if (data?.addMessage == null) {
                // handle possible error
            }
        }
        mainChannelMessageList.notifyObserver()

    }

    // Singleton
    companion object {
        @Volatile
        private var instance: ChatRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val chatDataSource = ChatDataSource()
            instance ?: ChatRepository(chatDataSource, UserRepository.getInstance()).also {
                instance = it
            }
        }
    }

    // Function to make sure observer is notified when a data structure is modified
    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}