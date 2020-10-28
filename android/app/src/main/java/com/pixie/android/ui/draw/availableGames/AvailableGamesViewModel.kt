package com.pixie.android.ui.draw.availableGames

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language

class AvailableGamesViewModel(private val gameRepository: GameRepository,private val gameSessionRepository:GameSessionRepository) : ViewModel() {

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): GameSessionData?{
        val game= gameRepository.createGame(mode, difficulty, language)
        if(game!=null)  gameSessionRepository.subscribeToGameSessionChange(game.id)
        return game
    }

    fun exitGame(gameID: Double){
        gameRepository.exitGame(gameID)
    }

    fun joinGame(gameID: Double): CreatedGameData?{
        return gameRepository.joinGame(gameID)
    }

    fun getAvailableGames() = gameRepository.getAvailableGames()

    fun fetchAvailableGames(mode:GameMode, difficulty: GameDifficulty){
        gameRepository.fetchAvailableGames(mode, difficulty)
    }
    
}