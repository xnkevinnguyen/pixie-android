package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.model.chat.ChannelData

class ChannelViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    fun getUserChannels() = chatRepository.getUserChannels()

    fun getCurrentChannelInfo(channelID: Double?): ChannelData? {
        return getUserChannels().value?.get(channelID)
    }
}