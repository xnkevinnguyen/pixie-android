package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.model.chat.ChannelParticipant

class PlayersViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun getMainChannelParticipants()= chatRepository.getMainChannelParticipantList()
    fun getCurrentChannelParticipants(channelID:Double?):ArrayList<ChannelParticipant>{
        val participantList = chatRepository.getUserChannels().value?.filter {
            it.channelID.equals(channelID)
        }?.get(0)?.participantList
        if(participantList.isNullOrEmpty()){
            return arrayListOf()
        }else{
            return ArrayList(participantList)
        }

    }
    fun getCurrentChannelParticipants():ArrayList<ChannelParticipant>{
        val currentChannelID= chatRepository.getCurrentChannelID().value
     return getCurrentChannelParticipants(currentChannelID)

    }    fun getUserChannels()=chatRepository.getUserChannels()

}