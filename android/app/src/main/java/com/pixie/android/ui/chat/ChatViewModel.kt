package com.pixie.android.ui.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun setCurrentChannelID(id: Double) {
        chatRepository.setCurrentChannelID(id)
    }

    fun getChannelMessageList() = chatRepository.getChannelMessages()
    fun getCurrentChannelMessageList(channelID: Double): ArrayList<MessageData> {
        val messageList = chatRepository.getChannelMessages().value?.get(channelID)?.messageList
        if (messageList.isNullOrEmpty()) {
            return arrayListOf()
        } else {
            return messageList
        }
    }

    fun getChatHistoryCurrentChannel() {
        val channelID = chatRepository.getCurrentChannelID().value
        if (channelID != null) {
            chatRepository.getChatHistory(channelID)
        }
    }

    fun getJoinableChannels() = chatRepository.getJoinableChannels()

    fun joinChannel(channelID: Double) {
        chatRepository.joinChannel(channelID)
    }

    fun createChannel(channelName: String) {
        return chatRepository.createChannel(channelName)
    }

    fun exitChannel(channelID: Double) {
        chatRepository.exitChannel(channelID)
    }

    fun getUserChannels() = chatRepository.getUserChannels()

    fun startChannels() {
        // Fetch all channels users has joined
        chatRepository.fetchUserChannels()
        chatRepository.suscribeToUserChannelListChanges()
    }


    fun stopChannel() {
        chatRepository.clearChannels()

    }

    fun sendMessageToCurrentChannel(message: String) {
        val channelID = chatRepository.getCurrentChannelID().value
        if (channelID != null) {
            chatRepository.sendMessage(channelID, message)
        }
    }

}