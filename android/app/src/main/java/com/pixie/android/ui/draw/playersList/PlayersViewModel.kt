package com.pixie.android.ui.draw.channelList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.follow.FollowRepository
import com.pixie.android.model.chat.ChannelParticipant
import java.lang.IndexOutOfBoundsException

class PlayersViewModel(private val chatRepository: ChatRepository, private val followRepository: FollowRepository) : ViewModel() {
    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun getCurrentChannelParticipants(channelID: Double?): ArrayList<ChannelParticipant> {

        val participantList = getUserChannels().value?.get(channelID)?.participantList
        if (participantList != null) {
            return ArrayList(participantList)
        }
        return arrayListOf()
    }

    fun getCurrentChannelParticipants(): ArrayList<ChannelParticipant> {
        val currentChannelID = chatRepository.getCurrentChannelID().value
        return getCurrentChannelParticipants(currentChannelID)

    }

    fun getUserChannels() = chatRepository.getUserChannels()

    fun addUserFollowList(user:ChannelParticipant) = followRepository.addUserFollowList(user)

    fun removeUserFollowList(user:ChannelParticipant) = followRepository.removeUserFollowList(user)

    fun isUserInFollowList(user:ChannelParticipant): Boolean = followRepository.isUserInFollowList(user)

}