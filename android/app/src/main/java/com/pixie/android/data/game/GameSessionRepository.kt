package com.pixie.android.data.game

import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.draw.CanvasCommandHistoryRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.game.GameParticipant
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.GameStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameSessionRepository(
    private val dataSource: GameSessionDataSource,
    private val userRepository: UserRepository,
    private val canvasCommandHistoryRepository: CanvasCommandHistoryRepository
) {
    private var gameSession = MutableLiveData<GameSessionData>()
    private var channelID: Double = 0.0
    private var gameSessionSubscription: Job? = null
    private var timerSubscription: Job? = null
    private var pathSubscription:Job?=null

    private var timer = MutableLiveData<Int>()

    fun getTimer() = timer


    fun getGameSession() = gameSession

    fun getGameChannelID() = channelID


    fun startGame(gameID: Double, onResult: (RequestResult) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            val gameSessionStarted = dataSource.startGame(gameID, userRepository.getUser().userId);
            if (gameSessionStarted != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    gameSession.postValue(gameSessionStarted)
                    channelID = gameSessionStarted.channelID
                    onResult(RequestResult(true))
                    //start additional subscriptions
                    subscribeToTimer(gameID)
                    subscribeToGameSessionChange(gameID)
                    subscribeToPathChange(gameID)

                }
            }
        }
    }

    fun subscribeToTimer(gameID: Double) {

        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.suscribeToTimer(gameID, userRepository.getUser().userId) {
                CoroutineScope(Dispatchers.Main).launch {
                    timer.postValue(it.toInt())
                }

            }
        }
        timerSubscription?.cancel()
        timerSubscription = job
    }

    fun subscribeToGameSessionChange(gameID: Double) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToGameSessionChange(gameID, userRepository.getUser().userId) {
                CoroutineScope(Dispatchers.Main).launch {
                    // Handles the case where another user starts the game
                    if (gameSession.value == null || gameSession.value?.status == GameStatus.PENDING || gameSession.value?.status == GameStatus.READY) {
                        channelID = it.channelID
                        subscribeToTimer(it.id)
                        subscribeToPathChange(gameID)

                    }
                    //handles going to the next round
                    if(gameSession.value?.currentRound!=null && it.currentRound>gameSession.value!!.currentRound!! ){
                        canvasCommandHistoryRepository.clear()
                    }
                    gameSession.postValue(it)

                }
            }
        }
        gameSessionSubscription?.cancel()
        gameSessionSubscription = job
    }
    fun subscribeToPathChange(gameID:Double){
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToPathChange(gameID,userRepository.getUser().userId){
                CoroutineScope(Dispatchers.Main).launch {
                    canvasCommandHistoryRepository.addCanvasCommand(it)
                }
            }
        }
        pathSubscription?.cancel()
        pathSubscription= job
    }

    fun guessWord(word: String, onResult: (Boolean?)->Unit) {
        val gameID = gameSession.value?.id
        if(gameID!=null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = dataSource.guessWord(word, gameID, userRepository.getUser().userId)
                CoroutineScope(Dispatchers.Main).launch {
                onResult(response)}
            }
        }
    }
    fun leaveGameIfRunning():Boolean{
        if(pathSubscription !=null && timerSubscription!=null) {
            gameSessionSubscription?.cancel()
            timerSubscription?.cancel()
            pathSubscription?.cancel()
            gameSessionSubscription = null
            timerSubscription = null
            pathSubscription = null
            gameSession = MutableLiveData()
            return true
        }else{
            return false
        }
    }

    fun leaveGame() {

        gameSessionSubscription?.cancel()
        timerSubscription?.cancel()
        pathSubscription?.cancel()
        gameSessionSubscription = null
        timerSubscription = null
        pathSubscription=null
        gameSession = MutableLiveData()
        // send leave request
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: GameSessionRepository? = null
        private var dataSource: GameSessionDataSource = GameSessionDataSource()
        private var drawCommandHistoryRepository = CanvasCommandHistoryRepository.getInstance()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: GameSessionRepository(dataSource, UserRepository.getInstance(),
                drawCommandHistoryRepository).also {
                instance = it
            }
        }
    }
}