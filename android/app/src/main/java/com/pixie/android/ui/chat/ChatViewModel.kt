package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository

class ChatViewModel (private val chatRepository: ChatRepository): ViewModel(){


    fun getMainChannelMessage() = chatRepository.getMainChannelMessageList()

    fun getMainChannelParticipants()= chatRepository.getMainChannelParticipantList()

    fun getUserChannels()=chatRepository.getUserChannels()

    fun startChannels(){
        // Fetch all channels users has joined
        chatRepository.fetchUserChannels()
        chatRepository.enterMainChannel()
        chatRepository.subscribeChannelMessages()
        chatRepository.suscribeChannelUsers()

    }
    fun stopChannel(){
       chatRepository.cancelMainChannelSubscriptions()
        chatRepository.exitMainChannel()
        chatRepository.clearChannels()

    }

    fun sendMessageToCurrentChannel(message:String){
        chatRepository.sendMessage(message)
    }

}