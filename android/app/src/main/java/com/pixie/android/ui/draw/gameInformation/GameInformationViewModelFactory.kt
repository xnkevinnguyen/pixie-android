package com.pixie.android.ui.draw.gameInformation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModel

@Suppress("UNCHECKED_CAST")
class GameInformationViewModelFactory(private val gameSessionRepository: GameSessionRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameInformationViewModel(gameSessionRepository) as T
    }
}