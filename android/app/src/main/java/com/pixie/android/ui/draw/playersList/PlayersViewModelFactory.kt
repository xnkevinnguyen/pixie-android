package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.follow.FollowRepository

@Suppress("UNCHECKED_CAST")
class PlayersViewModelFactory(private val chatRepository: ChatRepository, private val followRepository: FollowRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayersViewModel(chatRepository, followRepository) as T
    }
}