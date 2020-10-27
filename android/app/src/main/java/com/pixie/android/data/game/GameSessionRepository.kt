package com.pixie.android.data.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
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
    // should be included in start game response
    private var timer = MutableLiveData<Int>()

    fun getTimer() = timer

    fun getGameSession()=gameSession


    fun startGame(gameID:GameID, onResult:(RequestResult)->Unit){
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
                }
            }
        }
    }
    fun subscribeToTimer(gameID: GameID){
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.suscribeToTimer(gameID,userRepository.getUser().userId){
                CoroutineScope(Dispatchers.Main).launch {
                    timer.postValue(it.toInt())
                }

            }
        }
        gameSubscriptions.add(job)
    }

    fun subscribeToGameSessionChange(gameID: GameID){
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