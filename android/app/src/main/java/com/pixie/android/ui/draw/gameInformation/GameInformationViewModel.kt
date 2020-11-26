package com.pixie.android.ui.draw.gameInformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.game.GameSessionData

class GameInformationViewModel( private val gameSessionRepository: GameSessionRepository,private val chatRepository: ChatRepository,private val gameRepository: GameRepository):
    ViewModel() {

    fun getTimer(): LiveData<Int> =gameSessionRepository.getTimer()

    fun getGameSession():LiveData<GameSessionData> = gameSessionRepository.getGameSession()

//    fun getPlayers():LiveData<ArrayList<GamePlayerData>> = gameSessionRepository.getPlayers()

    fun shouldDisplayHints():Boolean{
        return !gameSessionRepository.isUserDrawingTurn()
    }
    fun leaveGame(){
        gameSessionRepository.leaveGame()
    }

    fun leaveGameIfNecessary(){
        if(gameSessionRepository.leaveGameIfRunning()){
            val gameID = getGameSession().value?.id
            val channelID = getGameSession().value?.channelID
            if (channelID != null) {
                chatRepository.exitChannel(channelID)
            }
            if (gameID != null) {
                gameRepository.exitGame(gameID)
            }
        }
    }

    fun askHint(){
        gameSessionRepository.askHint()
    }

}