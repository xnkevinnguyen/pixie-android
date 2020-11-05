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
                val players = ArrayList(data.gameInfo.scores!!.map {
                    GameParticipant(it.user.id, it.user.username, it.user.isOnline,it.value!!,it.user.isVirtual!! )
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
                    data.gameInfo.mode,
                    data.gameState
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
                    val players = ArrayList(data.gameInfo.scores!!.map {
                        GameParticipant(it.user.id, it.user.username, it.user.isOnline,it.value!!,it.user.isVirtual!! )
                    })
                    val gameSession = GameSessionData(
                        data.id,
                        data.currentDrawerId,
                        data.currentWord,
                        data.currentRound,
                        data.sprintTries!!,
                        data.status,
                        data.gameHall.id,
                        players,
                        data.gameInfo.mode,
                        data.gameState
                    )
                    onGameSessionChange(gameSession)
                }
            }
    }

    suspend fun subscribeToPathChange(
        gameSessionID: Double,
        userID: Double,
        onPathChange: (id:Double,command:CanvasCommand) -> Unit
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
                    onPathChange(data.id,command)
                }

            }
    }

    suspend fun sendManualDraw(
        gameSessionID: Double,
        userID: Double,
        pathPointInput: ManualPathPointInput,
        pathIDGenerator:Double
    ): Double? {
        val pathUniqueID = 1000000*userID+gameSessionID*10000+pathIDGenerator
        val input: ManualDrawingInput = ManualDrawingInput(
            gameSessionID,
            pathUniqueID,
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
                return pathUniqueID
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
                //TODO replace for the correct if statement and apply correct color
//                if (data?.commandStatus == CommandStatus.NONE && data.point != null && data.strokeWidth != null && data.commandPathId != null) {
                if ( data?.point != null && data.strokeWidth != null ) {
                    // Handles adding a point
                    var colorStroke = data.strokeColor?.toInt()
                    if (colorStroke ==null){
                        colorStroke = Color.BLACK
                    }
                    val paint = Paint().apply {
                        color = colorStroke
                        style = Paint.Style.STROKE
                        strokeJoin = Paint.Join.ROUND
                        strokeCap = Paint.Cap.ROUND
                        strokeWidth = data.strokeWidth.toFloat()
                    }
                    val drawPoint = ManualDrawingPoint(
                        data.currentPathId,
                        data.point.x.toFloat(),
                        data.point.y.toFloat(),
                        status = data.status,
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