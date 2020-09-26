package com.pixie.android.ui.draw.chat

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository

class ChatViewModel (private val chatRepository: ChatRepository): ViewModel(){


    fun getMainChannelMessage() = chatRepository.getMainChannelMessageList()

    fun suscribeToChannel(){
        chatRepository.subscribeChannelMessages()
    }
    fun sendMessageToCurrentChannel(message:String){
        chatRepository.sendMessage(message)
    }

}