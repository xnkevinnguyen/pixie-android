package com.pixie.android.data.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameData
import com.pixie.android.model.game.GamePlayerData
import com.pixie.android.model.game.GameSessionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameSessionRepository (
    private val dataSource:GameSessionDataSource,
    private val userRepository: UserRepository
){
    private var gameSession= MutableLiveData<GameSessionData>()
    private  var channelID:Double =0.0
    private var gameSubscriptions= arrayListOf<Job>()
    private var players = MutableLiveData<ArrayList<GamePlayerData>>().apply { this.postValue(
        arrayListOf()) }

    // should be included in start game response
    private var timer = MutableLiveData<Int>()

    fun getTimer() = timer

    fun getPlayers() = players

    fun getGameSession()=gameSession

    fun getGameChannelID() = channelID


    fun startGame(gameID:Double, onResult:(RequestResult)->Unit){
        gameSubscriptions.clear()
        players.postValue(arrayListOf())
        CoroutineScope(Dispatchers.IO).launch{
            val gameSessionStarted = dataSource.startGame(gameID,userRepository.getUser().userId);
            if(gameSessionStarted !=null){
                CoroutineScope(Dispatchers.Main).launch {
                    gameSession.postValue(gameSessionStarted)
                    channelID = gameSessionStarted.channelID
                    onResult(RequestResult(true))
                    //start additional subscriptions
                    subscribeToTimer(gameID)
                    subscribeToGameSessionChange(gameID)

                    for(player in gameSessionStarted.players) {
                        val gamePlayerData = GamePlayerData(player.id, player.username, player.isOnline, 0.0)
                        players.value?.add(gamePlayerData)
                    }
                }
            }
        }
    }
    fun subscribeToTimer(gameID: Double){
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.suscribeToTimer(gameID,userRepository.getUser().userId){
                CoroutineScope(Dispatchers.Main).launch {
                    timer.postValue(it.toInt())
                }

            }
        }
        gameSubscriptions.add(job)
    }

    fun subscribeToGameSessionChange(gameID: Double){
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToGameSessionChange(gameID,userRepository.getUser().userId){
                CoroutineScope(Dispatchers.Main).launch {
                    gameSession.postValue(it)
                }
            }
        }
        gameSubscriptions.add(job)
    }
    // Singleton
    companion object {
        @Volatile
        private var instance: GameSessionRepository? = null
        private var dataSource: GameSessionDataSource = GameSessionDataSource()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: GameSessionRepository(dataSource, UserRepository.getInstance()).also {
                instance = it
            }
        }
    }
}