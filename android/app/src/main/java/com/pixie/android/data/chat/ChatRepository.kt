package com.pixie.android.data.chat

import android.util.Log
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
import kotlin.collections.HashMap

class ChatRepository(
    private val dataSource: ChatDataSource,
    private val userRepository: UserRepository
) {

    val MAIN_CHANNEL_ID = 1.0
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf())
    }
    // Map with <ChannelID,Messages>
    private var channelMessages = MutableLiveData<MutableMap<Double,ArrayList<MessageData>>>().apply{
        val initialMap = HashMap<Double,ArrayList<MessageData>>()
        val messageList = arrayListOf<MessageData>(MessageData("message1",false,"user","100000"),MessageData("message1",false,"user","100000"))

        val messageList2 = arrayListOf<MessageData>(MessageData("message2",false,"user","100000"))

        initialMap.put(1.0, messageList)
        initialMap.put(2.0,messageList2)
        this.postValue(initialMap)
    }
    private var channelSubscriptions:ArrayList<Job> = arrayListOf()

    private var mainChannelParticipantList =
        MutableLiveData<MutableList<ChannelParticipant>>().apply {
            val loadingUsername = "Loading ..."
            val loadingID = 1.0
            this.postValue(arrayListOf(ChannelParticipant(loadingID, loadingUsername, false)))
        }
    private var userChannels = MutableLiveData<ArrayList<ChannelData>>()
    private var currentChannelID = MutableLiveData<Double>()


    private lateinit var mainChannelMessageJob: Job
    private lateinit var mainChannelParticipantJob: Job

    fun getCurrentChannelID():LiveData<Double>{
        return currentChannelID
    }

    fun getChannelMessages():LiveData<MutableMap<Double,ArrayList<MessageData>>>{
        return channelMessages
    }
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
            userChannels.postValue(ArrayList(channelDataList))
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
    fun setCurrentChannelID(id:Double){
        currentChannelID.postValue(id)
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
            dataSource.suscribeToChannelMessages(userRepository.getUser().userId,MAIN_CHANNEL_ID, onReceiveMessage = {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    it.belongsToCurrentUser = it.userName == userRepository.getUser().username
                    mainChannelMessageList.value?.add(it)
                        mainChannelMessageList.notifyObserver()
                    }


            })
        }
    }


    fun suscribeChannelUsers() {
        mainChannelParticipantJob = CoroutineScope(IO).launch {
            dataSource.suscribeToChannelChange(userRepository.getUser().userId,MAIN_CHANNEL_ID, onChannelChange = {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    mainChannelParticipantList.postValue(it.participantList.toMutableList())
                }

            })
        }
    }


    fun sendMessage(message: String) {
        CoroutineScope(IO).launch {
            val data = dataSource.sendMessageToChannel(
                message,
                MAIN_CHANNEL_ID,
                userRepository.getUser().userId
            )
            if (data?.addMessage == null) {
                // handle possible error
            }
        }
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