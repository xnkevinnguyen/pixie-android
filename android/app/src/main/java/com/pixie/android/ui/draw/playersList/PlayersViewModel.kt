package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository

class PlayersViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    fun getMainChannelParticipants()= chatRepository.getMainChannelParticipantList()
}