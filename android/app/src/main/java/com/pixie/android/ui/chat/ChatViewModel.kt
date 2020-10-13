package com.pixie.android.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.model.chat.MessageData

class ChatViewModel (private val chatRepository: ChatRepository): ViewModel(){

    fun getCurrentChannelID():LiveData<Double> = chatRepository.getCurrentChannelID()

    fun setCurrentChannelID(id:Double){
        chatRepository.setCurrentChannelID(id)
    }
    fun getChannelMessageList() = chatRepository.getChannelMessages()
    fun getCurrentChannelMessageList(channelID:Double):ArrayList<MessageData>{
        val messageList=chatRepository.getChannelMessages().value?.get(channelID)
        if(messageList.isNullOrEmpty()){
            return arrayListOf()
        }else{
            return messageList
        }
    }

    fun getMainChannelMessage() = chatRepository.getMainChannelMessageList()

    fun getMainChannelParticipants()= chatRepository.getMainChannelParticipantList()

    fun getUserChannels()=chatRepository.getUserChannels()

    fun startChannels(){
        // Fetch all channels users has joined
        chatRepository.fetchUserChannels()
        chatRepository.subscribeChannelMessages()
        chatRepository.suscribeChannelUsers()
        chatRepository.suscribeToUserChannelListChanges()
    }

    fun stopChannel(){
//       chatRepository.cancelMainChannelSubscriptions()
//        chatRepository.exitMainChannel()
        chatRepository.clearChannels()

    }

    fun sendMessageToCurrentChannel(message:String){
        val channelID =chatRepository.getCurrentChannelID().value
        if(channelID!=null) {
            chatRepository.sendMessage(channelID,message)
        }
    }

}