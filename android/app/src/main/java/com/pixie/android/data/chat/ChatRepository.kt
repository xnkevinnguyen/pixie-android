package com.pixie.android.data.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ChatRepository(
    private val dataSource: ChatDataSource,
    private val userRepository: UserRepository
) {


    val MAIN_CHANNEL_ID = 3.0
    val CHAT_INTRO = "Welcome to the chat! :)"
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf(MessageData(CHAT_INTRO, false, "Pixie")))
    }
    private var mainChannelParticipantList = MutableLiveData<MutableList<ChannelParticipant>>().apply {
        val userID = userRepository.user?.userId
        val username = userRepository.user?.username
        if (userID != null && username != null) {
            this.postValue(arrayListOf(ChannelParticipant(userID, username, true)))

        }
    }

    fun getMainChannelMessageList(): LiveData<MutableList<MessageData>> {
        return mainChannelMessageList
    }

    fun getMainChannelParticipantList():LiveData<MutableList<ChannelParticipant>>{
        return mainChannelParticipantList
    }

    fun subscribeChannelMessages() {
        CoroutineScope(IO).launch {
            dataSource.suscribeToChannelMessages(MAIN_CHANNEL_ID) {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    if (it.userName != userRepository.user!!.username) {
                        mainChannelMessageList.value?.add(it)
                        mainChannelMessageList.notifyObserver()
                    }
                }

            }
        }
    }

    fun suscribeChannelUsers() {
        CoroutineScope(IO).launch {
            dataSource.suscribeToChannelChange(MAIN_CHANNEL_ID) {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    mainChannelParticipantList.postValue(it.participantList.toMutableList())
                }
            }

        }
    }


    fun sendMessage(message: String) {
        val messageData = MessageData(message, true)
        mainChannelMessageList.value?.add(messageData)
        CoroutineScope(IO).launch {
            val data = dataSource.sendMessageToChannel(
                messageData,
                MAIN_CHANNEL_ID,
                userRepository.user!!.userId
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