package com.pixie.android.ui.draw.availableGames

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language

class AvailableGamesViewModel(private val gameRepository: GameRepository) : ViewModel() {

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): CreatedGameData?{
        return gameRepository.createGame(mode, difficulty, language)
    }

    fun exitGame(gameID: Double){
        gameRepository.exitGame(gameID)
    }

    fun getAvailableGames() = gameRepository.getAvailableGames()

    fun fetchAvailableGames(mode:GameMode, difficulty: GameDifficulty){
        gameRepository.fetchAvailableGames(mode, difficulty)
    }
}