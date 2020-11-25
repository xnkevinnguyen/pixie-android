package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.draw.CommandType
import com.pixie.android.model.draw.ManualPathPointInput
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.CommandStatus
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameState
import com.pixie.android.type.GameStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameSessionRepository(
    private val dataSource: GameSessionDataSource,
    private val userRepository: UserRepository,
    private val canvasRepository: CanvasRepository,
    private val chatRepository: ChatRepository
) {
    private var gameSession = MutableLiveData<GameSessionData>()

    // word is displayed to the user who is drawing
    private var shouldShowWord = MutableLiveData<ShowWordinGame>()
    private var channelID: Double = 0.0
    private var gameSessionSubscription: Job? = null
    private var timerSubscription: Job? = null
    private var pathSubscription: Job? = null
    private var manualPathSubscription: Job? = null
    private var pathIDGenerator = 0.0
    private var pathOrderGenerator = 0.0
    private var isCanvasLocked = MutableLiveData<Boolean>()


    private var timer = MutableLiveData<Int>()

    fun getTimer() = timer


    fun getGameSession() = gameSession

    fun getIsCanvasLocked() = isCanvasLocked

    // used for the current user to display current word to draw
    fun getShouldShowWord() = shouldShowWord

    fun getGameSessionID(): Double {
        val gameSessionID = gameSession.value?.id
        if (gameSessionID != null) {
            return gameSessionID
        } else {
            throw error("GameSessionID is null")
        }
    }

    fun setGameSession(newGameSession: GameSessionData) {
        gameSession.postValue(newGameSession)
    }

    fun getGameChannelID() = channelID

    // returns true if the user should be drawing
    fun isUserDrawingTurn(): Boolean {
        return gameSession.value?.currentDrawerId == userRepository.getUser().userId
    }


    fun startGame(gameID: Double, onResult: (RequestResult) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            subscribeToTimer(gameID)
            val gameSessionStarted = dataSource.startGame(gameID, userRepository.getUser().userId);
            if (gameSessionStarted != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    gameSession.postValue(gameSessionStarted)
                    channelID = gameSessionStarted.channelID
                    onResult(RequestResult(true))
                    //start additional subscriptions

                    subscribeToPathChange(gameID, gameSessionStarted.mode)

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
                        subscribeToPathChange(gameID, it.mode)

                    }
                    //handles going to the next round or when drawer switches
                    if (gameSession.value?.currentRound != null && (it.currentRound > gameSession.value!!.currentRound!! ||
                                (it.currentDrawerId != gameSession.value?.currentDrawerId && gameSession.value!!.currentDrawerId!! >= 0.0))
                    ) {
                        canvasRepository.clear()
                    }

                    if (it.state == GameState.DRAWER_SELECTION) {
                        Log.d("GameSessionRepository", it.currentDrawerId.toString() + " ")
                        if (it.currentDrawerId == userRepository.getUser().userId) {
                            shouldShowWord.postValue(
                                ShowWordinGame(
                                    true,
                                    ShowWordinGameType.IS_DRAWER,
                                    it.currentWord
                                )
                            )
                        } else {
                            val currentDrawerList =
                                it.players.filter { gameParticipant ->
                                    Log.d(
                                        "GameSessionRepository",
                                        it.currentDrawerId.toString() + " " + gameParticipant.id
                                    )

                                    gameParticipant.id == it.currentDrawerId
                                }

                            if (!currentDrawerList.isEmpty())
                                shouldShowWord.postValue(
                                    ShowWordinGame(
                                        true,
                                        ShowWordinGameType.OTHER_DRAWER,
                                        currentDrawerList.first().username
                                    )
                                )
                        }

                    } else if (it.state == GameState.DRAWING && it.currentDrawerId == userRepository.getUser().userId) {
                        shouldShowWord.postValue(
                            ShowWordinGame(
                                false,
                                ShowWordinGameType.NONE,
                                it.currentWord
                            )
                        )
                    } else {
                        shouldShowWord.postValue(ShowWordinGame(false, ShowWordinGameType.NONE))

                    }
                    if (it.state == GameState.DRAWING && it.currentDrawerId == userRepository.getUser().userId) {
                        isCanvasLocked.postValue(false)
                    } else {
                        isCanvasLocked.postValue(true)
                    }
                    gameSession.postValue(it)

                }
            }
        }
        gameSessionSubscription?.cancel()
        gameSessionSubscription = job
    }

    fun sendManualDrawingPoint(pathPointInput: ManualPathPointInput) {
        //Only send a manual drawing if it's user's turn
//        if(isUserDrawingTurn() && gameSession.value?.mode == GameMode.FREEFORALL){
        if (gameSession.value?.mode == GameMode.FREEFORALL) {
            val order = pathOrderGenerator
            CoroutineScope(Dispatchers.IO).launch {
                dataSource.sendManualDraw(
                    getGameSessionID(),
                    userRepository.getUser().userId,
                    pathPointInput,
                    pathIDGenerator,
                    order
                )

                }
            pathOrderGenerator += 1;


        }
    }

    fun sendManualDrawingFinalPoint(
        pathPointInput: ManualPathPointInput,
        onResult: (Double) -> Unit
    ) {
        //Only send a manual drawing if it's user's turn
//        if(isUserDrawingTurn() && gameSession.value?.mode == GameMode.FREEFORALL && gameSession.value?.id !=null){
        if (gameSession.value?.mode == GameMode.FREEFORALL && gameSession.value?.id != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val pathID = dataSource.sendManualDraw(
                    getGameSessionID(),
                    userRepository.getUser().userId,
                    pathPointInput,
                    pathIDGenerator,
                    pathOrderGenerator
                )
                if (pathID != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        onResult(pathID)
                        pathIDGenerator += 1
                        pathOrderGenerator = 0.0
                    }

                }
            }
        }
    }

    fun sendManualCommand(
        commandType: CommandType,
        pathID: Double? = null

    ) {
        if (gameSession.value?.mode == GameMode.FREEFORALL && gameSession.value?.id != null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (commandType.equals(CommandType.ERASE) && pathID != null) {
                    dataSource.sendManualCommand(
                        getGameSessionID(), userRepository.getUser().userId,
                        CommandStatus.DELETE, pathID
                    )
                } else if (commandType.equals(CommandType.UNDO)) {
                    dataSource.sendManualCommand(
                        getGameSessionID(), userRepository.getUser().userId,
                        CommandStatus.UNDO
                    )
                } else if (commandType.equals(CommandType.REDO)) {
                    dataSource.sendManualCommand(
                        getGameSessionID(), userRepository.getUser().userId,
                        CommandStatus.REDO
                    )
                }
            }
        }
    }


    fun subscribeToPathChange(gameID: Double, mode: GameMode) {
        if (mode == GameMode.FREEFORALL) {
            subscribeToManualDrawing(gameID)
        }
//        else if (mode == GameMode.COOP || mode == GameMode.SOLO) {
        // Handle Solo-Coop drawing
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToVirtualPlayerDrawing(
                gameID,
                userRepository.getUser().userId, onPathBegin = { id, command ->
                    CoroutineScope(Dispatchers.Main).launch {
                        canvasRepository.addCanvasCommand(id, command)
                    }
                }, onPathUpdate = { id, command ->
                    CoroutineScope(Dispatchers.Main).launch {
                        canvasRepository.appendCanvasCommand(id, command)
                    }
                })
        }
        pathSubscription?.cancel()
        pathSubscription = job
    }
//}

    fun subscribeToManualDrawing(gameID: Double) {
        // Handle Free for all drawing from OTHER players
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToManualDrawing(
                gameID,
                userRepository.getUser().userId,
                onDraw = {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Only draw on the canvas if it is not from the server
                        if (!isUserDrawingTurn())
                            canvasRepository.addManualDrawPoint(it)
                    }
                },
                onServerDrawHistoryCommand = {
                    canvasRepository.handleServerDrawHistoryCommand(it)
                })
        }
        manualPathSubscription?.cancel()
        manualPathSubscription = job
    }

    fun guessWord(word: String, onResult: (Boolean?) -> Unit) {
        val gameID = gameSession.value?.id
        if (gameID != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = dataSource.guessWord(word, gameID, userRepository.getUser().userId)
                CoroutineScope(Dispatchers.Main).launch {
                    onResult(response)
                }
            }
        }
    }

    fun leaveGameIfRunning(): Boolean {
        if (pathSubscription != null && timerSubscription != null) {
            leaveGame()
            return true
        } else {
            return false
        }
    }

    fun leaveGame() {
        val channelID = gameSession.value?.channelID
        if (channelID != null)
            chatRepository.exitChannel(channelID)
        canvasRepository.clear()
        gameSessionSubscription?.cancel()
        timerSubscription?.cancel()
        pathSubscription?.cancel()
        manualPathSubscription?.cancel()
        gameSessionSubscription = null
        timerSubscription = null
        pathSubscription = null
        manualPathSubscription = null
        gameSession = MutableLiveData()
        // send leave request
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: GameSessionRepository? = null
        private var dataSource: GameSessionDataSource = GameSessionDataSource()
        private var drawCommandHistoryRepository = CanvasRepository.getInstance()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: GameSessionRepository(
                dataSource, UserRepository.getInstance(),
                drawCommandHistoryRepository, ChatRepository.getInstance()
            ).also {
                instance = it
            }
        }
    }
}