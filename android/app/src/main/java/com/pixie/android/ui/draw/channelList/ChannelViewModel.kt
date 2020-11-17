package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.friend.FriendListRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant

class ChannelViewModel(private val chatRepository: ChatRepository, private val friendListRepository: FriendListRepository) : ViewModel() {

    fun getUserChannels() = chatRepository.getUserChannels()

    fun getCurrentChannelInfo(channelID: Double?): ChannelData? {
        return getUserChannels().value?.get(channelID)
    }


    fun getFriendList():LiveData<ArrayList<ChannelParticipant>>{
        return friendListRepository.getFriendList()
    }
}