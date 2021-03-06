package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.sound.SoundRepository

@Suppress("UNCHECKED_CAST")
class GameChatViewModelFactory(private val chatRepository: ChatRepository,
                                private val gameSessionRepository: GameSessionRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameChatViewModel(chatRepository,gameSessionRepository) as T
    }
}