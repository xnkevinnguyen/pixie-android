package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.everybody.EverybodyRepository
import com.pixie.android.data.friend.FriendListRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant

class ChannelViewModel(private val chatRepository: ChatRepository,
                        private val everybodyRepository: EverybodyRepository) : ViewModel() {

    fun getUserChannels() = chatRepository.getUserChannels()

    fun getCurrentChannelInfo(channelID: Double?): ChannelData? {
        return getUserChannels().value?.get(channelID)
    }

    fun getAllUsers():LiveData<ArrayList<ChannelParticipant>>{
        return everybodyRepository.getUserList()
    }
}