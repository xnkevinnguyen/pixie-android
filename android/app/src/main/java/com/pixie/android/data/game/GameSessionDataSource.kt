package com.pixie.android.data.game

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.draw.*
import com.pixie.android.model.game.GameParticipant
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen

class GameSessionDataSource {
    suspend fun startGame(gameID: Double, userID: Double): GameSessionData? {
        val startGameInput = StartGameInput(gameID)
        try {
            val response =
                apolloClient(userID).mutate(StartGameMutation(startGameInput)).toDeferred()
                    .await()
            val data = response.data?.startGame
            if (data != null && data.gameInfo.players != null) {
                val players = ArrayList(data.gameInfo.players.map {
                    GameParticipant(it.id, it.username, it.isOnline)
                })
                return GameSessionData(
                    data.id,
                    data.currentDrawerId,
                    data.currentWord,
                    data.currentRound,
                    data.sprintTries,
                    data.status,
                    data.gameHall.id,
                    players,
                    data.gameInfo.mode
                )
            }

        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
        return null
    }

    suspend fun suscribeToTimer(
        gameSessionID: Double,
        userID: Double,
        onTimerChange: (Double) -> Unit
    ) {
        apolloClient(userID).subscribe(OnTimerChangeSubscription(gameSessionID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }.collect {
                val timerDataChange = it.data?.onTimerChange
                if (timerDataChange != null) {
                    onTimerChange(timerDataChange.currentValue)
                }
            }
    }

    suspend fun subscribeToGameSessionChange(
        gameSessionID: Double,
        userID: Double,
        onGameSessionChange: (GameSessionData) -> Unit
    ) {
        apolloClient(userID).subscribe(OnGameSessionChangeSubscription(gameSessionID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }.collect {

                val data = it.data?.onGameSessionChange
                if (data != null && data.gameInfo.players != null) {
                    val players = ArrayList(data.gameInfo.players.map {
                        GameParticipant(it.id, it.username, it.isOnline)
                    })
                    data.gameInfo.scores?.forEach { score ->
                        players.forEach { gameParticipant ->
                            if (score.id == gameParticipant.id && score.value != null) {
                                gameParticipant.score = score.value
                            }
                        }
                    }
                    val gameSession = GameSessionData(
                        data.id,
                        data.currentDrawerId,
                        data.currentWord,
                        data.currentRound,
                        data.sprintTries!!,
                        data.status,
                        data.gameHall.id,
                        players,
                        data.gameInfo.mode
                    )
                    onGameSessionChange(gameSession)
                }
            }
    }

    suspend fun subscribeToPathChange(
        gameSessionID: Double,
        userID: Double,
        onPathChange: (CanvasCommand) -> Unit
    ) {

        apolloClient(userID).subscribe(OnPathChangeSubscription(gameSessionID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }.collect {
                val data = it.data?.onPathChange
                if (data != null && data.points != null) {
                    val dataPoints = data.points.trim().split(" ").map {
                        val pointString = it.split(",")
                        SinglePoint(pointString[0].toFloat(), pointString[1].toFloat())
                    }
                    val pathList = arrayListOf<PathPoint>()
                    val length = dataPoints.size
                    for (i in 0..length) {
                        if (i + 1 < length) {
                            val point = PathPoint(
                                dataPoints[i].x,
                                dataPoints[i].y,
                                dataPoints[i + 1].x,
                                dataPoints[i + 1].y
                            )
                            pathList.add(point)
                        }
                    }

                    val paint = Paint().apply {
                        color = Color.parseColor(data.primaryColor)
                        style = Paint.Style.STROKE
                        strokeJoin = Paint.Join.ROUND
                        strokeCap = Paint.Cap.ROUND
                        strokeWidth = data.strokeWidth.toFloat()
                    }
                    val command = CanvasCommand(CommandType.DRAW, paint, pathList)
                    onPathChange(command)
                }

            }
    }

    suspend fun sendManualDraw(
        gameSessionID: Double,
        userID: Double,
        pathPointInput: ManualPathPointInput
    ): Double? {
        val input: ManualDrawingInput = ManualDrawingInput(
            gameSessionID,
            DataPoints(pathPointInput.x.toDouble(), pathPointInput.y.toDouble()),
            pathPointInput.paint.color.toString().toInput(),
            pathPointInput.paint.strokeWidth.toDouble().toInput(),
            pathPointInput.pathStatus
        )
        try {
            Log.d("ManualDraw",pathPointInput.pathStatus.rawValue + " "+pathPointInput.x+","+pathPointInput.y)
            val response =
                apolloClient(userID).mutate(ManualDrawMutation(input)).toDeferred().await()
            val data = response.data
            if(data!=null){
                // TODO should return id in data
                return 1.0
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())
        }
        return null
    }

    suspend fun subscribeToManualDrawing(
        gameSessionID: Double,
        userID: Double,
        onDraw: (ManualDrawingPoint) -> Unit,
        onServerDrawHistoryCommand: (ServerDrawHistoryCommand) -> Unit
    ) {

        apolloClient(userID).subscribe(OnManualPlayerDrawingSubscription(gameSessionID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }.collect {
                val data = it.data?.onManualPlayerDrawing
                if (data?.commandStatus == CommandStatus.NONE && data.point != null && data.strokeWidth != null && data.commandPathId != null) {
                    // Handles adding a point
                    val paint = Paint().apply {
                        color = Color.parseColor(data.strokeColor)
                        style = Paint.Style.STROKE
                        strokeJoin = Paint.Join.ROUND
                        strokeCap = Paint.Cap.ROUND
                        strokeWidth = data.strokeWidth.toFloat()
                    }
                    val drawPoint = ManualDrawingPoint(
                        data.point.x.toFloat(),
                        data.point.y.toFloat(),

                        commandPathID = data.commandPathId,
                        status = data.status,
                        commandType = CommandType.DRAW,
                        paint = paint


                        )
                    onDraw(drawPoint)
                } else if (data?.commandStatus == CommandStatus.REDO && data.commandPathId != null) {
                    val serverDrawHistoryCommand =
                        ServerDrawHistoryCommand(data.commandPathId, CommandType.REDO)
                    onServerDrawHistoryCommand(serverDrawHistoryCommand)
                } else if (data?.commandStatus == CommandStatus.UNDO && data.commandPathId != null) {
                    val serverDrawHistoryCommand =
                        ServerDrawHistoryCommand(data.commandPathId, CommandType.UNDO)
                    onServerDrawHistoryCommand(serverDrawHistoryCommand)
                }
            }
    }

    suspend fun guessWord(
        word: String,
        gameSessionID: Double,
        userID: Double
    ): Boolean? {
        val guessWordInput = GuessWordInput(word, gameSessionID)
        try {
            val response =
                apolloClient(userID).mutate(GuessWordMutation(guessWordInput)).toDeferred().await()
            return response.data?.guessWord
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        return false
    }
}