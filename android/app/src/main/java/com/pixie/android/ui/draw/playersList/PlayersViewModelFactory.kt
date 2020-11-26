package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.friend.FriendListRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.user.UserRepository

@Suppress("UNCHECKED_CAST")
class PlayersViewModelFactory(
    private val chatRepository: ChatRepository,
    private val friendListRepository: FriendListRepository,
    private val gameInviteRepository: GameInviteRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userRepository: UserRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlayersViewModel(chatRepository, friendListRepository, gameInviteRepository, gameSessionRepository,userRepository) as T
    }
}