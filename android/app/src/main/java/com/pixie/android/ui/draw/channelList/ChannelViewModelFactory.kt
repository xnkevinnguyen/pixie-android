package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.everybody.EverybodyRepository
import com.pixie.android.data.friend.FriendListRepository

@Suppress("UNCHECKED_CAST")
class ChannelViewModelFactory(private val chatRepository: ChatRepository,
                              private val everybodyRepository: EverybodyRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChannelViewModel(chatRepository, everybodyRepository) as T
    }
}