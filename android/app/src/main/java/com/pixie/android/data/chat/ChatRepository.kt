package com.pixie.android.data.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.model.chat.MessageData

class ChatRepository {

    val CHAT_INTRO = "Welcome to the chat! :)"
    private var mainChannelMessageList = MutableLiveData<MutableList<MessageData>>().apply {
        this.postValue(arrayListOf(MessageData(CHAT_INTRO, false, "Pixie")))
    }

    fun getMainChannelMessageList(): LiveData<MutableList<MessageData>> {
        return mainChannelMessageList
    }

    fun createChannel() {

    }

    fun sendMessage(message: String) {
        val messageData = MessageData(message, true)
        mainChannelMessageList.value?.add(messageData)
        mainChannelMessageList.notifyObserver()

    }

    // Singleton
    companion object {
        @Volatile
        private var instance: ChatRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: ChatRepository().also {
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