package com.pixie.android.data.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.draw.CanvasCommandHistoryRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameData
import com.pixie.android.model.game.GamePlayerData
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
    private var players = MutableLiveData<ArrayList<GamePlayerData>>().apply {
        this.postValue(
            arrayListOf()
        )
    }

    // should be included in start game response
    private var timer = MutableLiveData<Int>()

    fun getTimer() = timer

    fun getPlayers() = players

    fun getGameSession() = gameSession

    fun getGameChannelID() = channelID


    fun startGame(gameID: Double, onResult: (RequestResult) -> Unit) {

        players.postValue(arrayListOf())
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

                    for (player in gameSessionStarted.players) {
                        val gamePlayerData =
                            GamePlayerData(player.id, player.username, player.isOnline, 0.0)
                        players.value?.add(gamePlayerData)
                    }
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
                        for (player in it.players) {
                            val gamePlayerData =
                                GamePlayerData(player.id, player.username, player.isOnline, 0.0)
                            players.value?.add(gamePlayerData)
                        }
                        subscribeToTimer(it.id)
                        subscribeToPathChange(gameID)

                    }

                    gameSession.postValue(it)

                }
            }
        }
        gameSessionSubscription?.cancel()
        gameSessionSubscription = job
    }
    fun subscribeToPathChange(gameID:Double){
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToPathChange(gameID,userRepository.getUser().userId){
                canvasCommandHistoryRepository.addCanvasCommand(it)
            }
        }
    }

    fun guessWord(word: String, onResult: (Boolean?)->Unit) {
        val gameID = gameSession.value?.id
        if(gameID!=null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = dataSource.guessWord(word, gameID, userRepository.getUser().userId)
                onResult(response)
            }
        }
    }


    fun leaveGame() {

        gameSessionSubscription?.cancel()
        timerSubscription?.cancel()
        gameSessionSubscription = null
        timerSubscription = null
        gameSession.value?.status = GameStatus.ENDED
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