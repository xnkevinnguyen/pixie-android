package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.follow.FollowRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameSessionRepository

@Suppress("UNCHECKED_CAST")
class PlayersViewModelFactory(
    private val chatRepository: ChatRepository, private val followRepository: FollowRepository,
    private val gameInviteRepository: GameInviteRepository,
    private val gameSessionRepository: GameSessionRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayersViewModel(chatRepository, followRepository,gameInviteRepository,gameSessionRepository) as T
    }
}