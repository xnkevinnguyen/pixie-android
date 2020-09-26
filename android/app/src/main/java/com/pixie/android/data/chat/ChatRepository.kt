package com.pixie.android.data.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.MessageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ChatRepository(private val dataSource: ChatDataSource,private val userRepository: UserRepository) {


    val MAIN_CHANNEL_ID = 3.0
    val CHAT_INTRO = "Welcome to the chat! :)"
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf(MessageData(CHAT_INTRO, false, "Pixie")))
    }

    fun getMainChannelMessageList(): LiveData<MutableList<MessageData>> {
        return mainChannelMessageList
    }

    fun subscribeChannelMessages() {
        CoroutineScope(IO).launch {
            dataSource.suscribeToChannel(MAIN_CHANNEL_ID) {
                // Main thread only used to modify values
                CoroutineScope(Main).launch {
                    if (it.userName !=userRepository.user!!.username){
                    mainChannelMessageList.value?.add(it)
                    mainChannelMessageList.notifyObserver()}
                }

            }
        }
    }

    fun sendMessage(message: String) {
        val messageData = MessageData(message, true)
        mainChannelMessageList.value?.add(messageData)
        CoroutineScope(IO).launch {
            val data = dataSource.sendMessageToChannel(messageData,MAIN_CHANNEL_ID,userRepository.user!!.userId)
            if(data?.addMessage ==null){
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