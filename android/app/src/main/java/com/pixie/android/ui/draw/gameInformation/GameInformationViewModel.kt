package com.pixie.android.ui.draw.gameInformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.game.GamePlayerData
import com.pixie.android.model.game.GameSessionData

class GameInformationViewModel( private val gameSessionRepository: GameSessionRepository):
    ViewModel() {

    fun getTimer(): LiveData<Int> =gameSessionRepository.getTimer()

    fun getGameSession():LiveData<GameSessionData> = gameSessionRepository.getGameSession()

    fun getPlayers():LiveData<ArrayList<GamePlayerData>> = gameSessionRepository.getPlayers()

    fun leaveGame(){
        gameSessionRepository.leaveGame()
    }

}