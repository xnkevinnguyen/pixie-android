package com.pixie.android.ui.draw.availableGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.ui.chat.ChatViewModel

@Suppress("UNCHECKED_CAST")
class AvailableGamesViewModelFactory(private val gameRepository: GameRepository,private val gameSessionRepository: GameSessionRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AvailableGamesViewModel(gameRepository,gameSessionRepository) as T
    }
}