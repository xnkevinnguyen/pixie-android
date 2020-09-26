package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository

class ChatViewModel (private val chatRepository: ChatRepository): ViewModel(){


    fun getMainChannelMessage() = chatRepository.getMainChannelMessageList()

    fun getMainChannelParticipants()= chatRepository.getMainChannelParticipantList()

    fun suscribeToChannelMessages(){
        chatRepository.subscribeChannelMessages()
    }
    fun suscribeToChannelChanges(){
        chatRepository.suscribeChannelUsers()
    }
    fun sendMessageToCurrentChannel(message:String){
        chatRepository.sendMessage(message)
    }

}