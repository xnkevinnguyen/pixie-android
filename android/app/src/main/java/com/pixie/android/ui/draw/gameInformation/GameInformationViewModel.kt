package com.pixie.android.ui.draw.gameInformation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.GameMode

class GameInformationViewModel( private val gameSessionRepository: GameSessionRepository,private val chatRepository: ChatRepository,private val gameRepository: GameRepository,
private val userRepository: UserRepository):
    ViewModel() {

    fun getTimer(): LiveData<Int> =gameSessionRepository.getTimer()

    fun getGameSession():LiveData<GameSessionData> = gameSessionRepository.getGameSession()

//    fun getPlayers():LiveData<ArrayList<GamePlayerData>> = gameSessionRepository.getPlayers()

    fun shouldDisplayHints(currentDrawerID:Double?,gameMode: GameMode):Boolean{
        return currentDrawerID != userRepository.getUser().userId || !gameMode.equals(GameMode.FREEFORALL)
    }

    fun isUserTheDrawer(currentDrawerID:Double?):Boolean{
        return currentDrawerID == userRepository.getUser().userId
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