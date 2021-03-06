package com.pixie.android.data.game

import android.graphics.Color
import android.graphics.Color.argb
import android.graphics.Paint
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
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
                    GameParticipant(
                        it.user.id,
                        it.user.username,
                        it.user.isOnline,
                        it.value!!,
                        it.user.isVirtual!!
                    )
                })
                var nHintsLeft: Int? = null
                if (data.gameInfo.mode == GameMode.COOP && data.sprintHints != null) {
                    nHintsLeft = data.sprintHints.toInt()
                } else {
                    data.gameInfo.players.forEach {
                        if (it.id == userID)
                            nHintsLeft = it.nHints
                    }
                }
                var guessesLeft: Int? = null

                data.gameInfo.scores?.forEach {
                    if (it.user.id == userID)
                        guessesLeft = it.tries.toInt()
                }
                return GameSessionData(
                    data.id,
                    data.currentDrawerId,
                    data.currentWord,
                    data.currentRound,
                    guessesLeft,
                    data.status,
                    data.gameHall.id,
                    players,
                    data.gameInfo.mode,
                    data.gameState,
                    nHintsLeft
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
                    var nHintsLeft: Int? = null
                    if (data.gameInfo.mode == GameMode.COOP && data.sprintHints != null) {
                        nHintsLeft = data.sprintHints.toInt()
                    } else {
                        data.gameInfo.players.forEach {
                            if (it.id == userID)
                                nHintsLeft = it.nHints
                        }
                    }
                    var guessesLeft: Int? = null

                    data.gameInfo.scores?.forEach {
                        if (it.user.id == userID)
                            guessesLeft = it.tries.toInt()
                    }
                    val players = ArrayList(data.gameInfo.scores!!.map {

                        GameParticipant(
                            it.user.id,
                            it.user.username,
                            it.user.isOnline,
                            it.value!!,
                            it.user.isVirtual!!
                        )
                    })

                    val winners = ArrayList(data.gameInfo.winners!!.map {

                        GameParticipant(
                            it.id,
                            it.username,
                            it.isOnline,
                            isVirtual = it.isVirtual!!
                        )
                    })
                    val gameSession = GameSessionData(
                        data.id,
                        data.currentDrawerId,
                        data.currentWord,
                        data.currentRound,
                        guessesLeft,
                        data.status,
                        data.gameHall.id,
                        players,
                        data.gameInfo.mode,
                        data.gameState,
                        nHintsLeft,
                        winners,
                        isCoopGuessSuccesful = data.sprintGuess?:false
                    )
                    onGameSessionChange(gameSession)
                }
            }
    }

    suspend fun subscribeToVirtualPlayerDrawing(
        gameSessionID: Double,
        userID: Double,
        onPathBegin: (id: Double, command: CanvasCommand) -> Unit,
        onPathUpdate: (id: Double, command: CanvasCommand) -> Unit
    ) {

        apolloClient(userID).subscribe(OnVirtualPlayerDrawingSubscription(gameSessionID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }.collect {
                val data: OnVirtualPlayerDrawingSubscription.OnVirtualPlayerDrawing? =
                    it.data?.onVirtualPlayerDrawing
                if (data != null) {
                    if (data.isPotrace == true) {
                        handlePotraceDrawing(data, onPathBegin, onPathUpdate)
                    } else {
                        handleRegularVirtualDrawing(data, onPathBegin, onPathUpdate)
                    }

                } else {
                    Log.d("OnVirtualPlayerDrawing", "Data is null")
                }

            }
    }

    private fun handlePotraceDrawing(
        data: OnVirtualPlayerDrawingSubscription.OnVirtualPlayerDrawing,
        onPathBegin: (id: Double, command: CanvasCommand) -> Unit,
        onPathUpdate: (id: Double, command: CanvasCommand) -> Unit
    ) {
        if (data.potracePoints != null) {
            val potraceDataPoints = ArrayList(data.potracePoints.map {
                val singlePoint = SinglePoint(it.x.toFloat(), it.y.toFloat(), it.order.toDouble())
                var command: PotraceCommand = PotraceCommand.M
                if (it.code.equals(PotraceCommand.M.toString())) {
                    command = PotraceCommand.M
                } else if (it.code.equals(PotraceCommand.C.toString())) {
                    command = PotraceCommand.C
                } else if (it.code.equals(PotraceCommand.L.toString())) {
                    command = PotraceCommand.L
                }
                val potraceDataPoint = PotraceDataPoint(
                    it.order.toDouble(), command, singlePoint, xAxisRotation = it.xAxisRotation,
                    largeArc = it.largeArc, sweep = it.sweep
                )
                if (it.x1 != null && it.x2 != null && it.y1 != null && it.y2 != null) {
                    val secondaryCoordinates = PathPoint(
                        it.x1.toFloat(),
                        it.y1.toFloat(),
                        it.x2.toFloat(),
                        it.y2.toFloat()
                    )
                    potraceDataPoint.secondaryCoordinates = secondaryCoordinates
                }
                potraceDataPoint
            })
            var colorStroke: Int? = null
            if (!data.strokeColor.isNullOrEmpty() && data.opacity != null) {
                colorStroke = Color.parseColor(data.strokeColor)
                colorStroke = argb(
                    (data.opacity * 255).toInt(),
                    colorStroke.red,
                    colorStroke.green,
                    colorStroke.blue
                )
            }
            if (colorStroke == null) {
                colorStroke = Color.BLACK
            }


            val paint = Paint().apply {
                color = colorStroke
                style = Paint.Style.FILL
                if (data.strokeWidth != null)
                    strokeWidth = data.strokeWidth.toFloat()
            }
            val command =
                CanvasCommand(CommandType.DRAW_POTRACE, paint, potracePoints = potraceDataPoints)
            if (data.status == PathStatus.BEGIN)
                onPathBegin(data.currentPathId, command)
            else
                onPathUpdate(data.currentPathId, command)
        }
    }

    private fun handleRegularVirtualDrawing(
        data: OnVirtualPlayerDrawingSubscription.OnVirtualPlayerDrawing,
        onPathBegin: (id: Double, command: CanvasCommand) -> Unit,
        onPathUpdate: (id: Double, command: CanvasCommand) -> Unit
    ) {
        if (data.points != null) {
            val dataPoints = ArrayList(data.points.map {
                SinglePoint(it.x.toFloat(), it.y.toFloat(), it.order)
            })

            var colorStroke: Int? = null
            if (!data.strokeColor.isNullOrEmpty() && data.opacity != null) {
                colorStroke = Color.parseColor(data.strokeColor)
                colorStroke = argb(
                    (data.opacity * 255).toInt(),
                    colorStroke.red,
                    colorStroke.green,
                    colorStroke.blue
                )
            }
            if (colorStroke == null) {
                colorStroke = Color.BLACK
            }

            val paint = Paint().apply {
                color = colorStroke
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
                if (data.strokeWidth != null)
                    strokeWidth = data.strokeWidth.toFloat()
            }
            val command = CanvasCommand(CommandType.DRAW, paint, dataPoints)

            if (data.status == PathStatus.BEGIN)
                onPathBegin(data.currentPathId, command)
            else
                onPathUpdate(data.currentPathId, command)

        }
    }


    suspend fun sendManualDraw(
        gameSessionID: Double,
        userID: Double,
        pathPointInput: ManualPathPointInput,
        pathUID: Double,
        pathOrderGenerator: Double
    ): Double? {

        val opacity: Double = pathPointInput.paint.alpha.toDouble() / 255
        val input: ManualDrawingInput = ManualDrawingInput(
            gameSessionID,
            pathUID,
            DataPoints(
                pathPointInput.x.toDouble(),
                pathPointInput.y.toDouble(),
                pathOrderGenerator
            ),
            ("#" + Integer.toHexString(pathPointInput.paint.color).substring(2)).toInput(),
            opacity.toInput(),
            pathPointInput.paint.strokeWidth.toDouble().toInput(),

            pathPointInput.pathStatus
        )
        try {
            Log.d(
                "ManualDrawingSend",
                pathOrderGenerator.toString() + " - (" + pathPointInput.x + "," + pathPointInput.y + ")"
            )

            val response =
                apolloClient(userID).mutate(ManualDrawMutation(input)).toDeferred().await()
            val data = response.data
            if (data != null) {
                return pathUID
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())
        }
        return null
    }

    suspend fun sendManualCommand(
        gameSessionID: Double,
        userID: Double,
        commandType: CommandStatus,
        pathID: Double? = null

    ) {
        val input = ManualCommandInput(
            pathID.toInput(),
            commandType.toInput(),
            gameSessionID.toInput()
        )
        try {
            val response =
                apolloClient(userID).mutate(ManualCommandMutation(input)).toDeferred().await()
            Log.d("sendManualCommand", response.toString())
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())
        }

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
//                if (data?.commandStatus == CommandStatus.NONE && data.point != null && data.strokeWidth != null && data.commandPathId != null) {
                if (data?.commandStatus == CommandStatus.NONE && data?.point != null && data.strokeWidth != null) {

                    var colorStroke: Int? = null
                    if (!data.strokeColor.isNullOrEmpty() && data.strokeOpacity != null) {
                        colorStroke = Color.parseColor(data.strokeColor)
                        colorStroke = argb(
                            (data.strokeOpacity * 255).toInt(),
                            colorStroke.red,
                            colorStroke.green,
                            colorStroke.blue
                        )
                    } else if (!data.strokeColor.isNullOrEmpty()) {
                        colorStroke = Color.parseColor(data.strokeColor)

                    }
                    if (colorStroke == null) {
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
                        data.point.order,
                        data.point.x.toFloat(),
                        data.point.y.toFloat(),
                        status = data.status,
                        paint = paint
                    )
                    Log.d(
                        "ManualDrawingReceive",
                        data.point.order.toString() + " - (" + data.point.x + "," + data.point.y + ")"
                    )
                    if (drawPoint.orderID >= 0)
                        onDraw(drawPoint)
                } else if (data?.commandStatus == CommandStatus.REDO && data.commandPathId != null) {
                    val serverDrawHistoryCommand =
                        ServerDrawHistoryCommand(data.commandPathId, CommandType.REDO)
                    onServerDrawHistoryCommand(serverDrawHistoryCommand)
                } else if (data?.commandStatus == CommandStatus.UNDO && data.commandPathId != null) {
                    val serverDrawHistoryCommand =
                        ServerDrawHistoryCommand(data.commandPathId, CommandType.UNDO)
                    onServerDrawHistoryCommand(serverDrawHistoryCommand)
                } else if (data?.commandStatus == CommandStatus.DELETE && data.commandPathId != null) {
                    val serverDrawHistoryCommand =
                        ServerDrawHistoryCommand(data.commandPathId, CommandType.ERASE)
                    onServerDrawHistoryCommand(serverDrawHistoryCommand)

                }
                if (data?.commandStatus == CommandStatus.NONE && data?.status == PathStatus.END)
                    Log.d(
                        "ManualCommand - ",
                        data?.commandStatus.toString() + "--" + data?.currentPathId.toString()
                    )
                if (data?.commandStatus != CommandStatus.NONE)
                    Log.d(
                        "ManualCommand - ",
                        data?.commandStatus.toString() + "--" + data?.commandPathId.toString()
                    )
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

    suspend fun askHint(
        gameSessionID: Double,
        userID: Double
    ): Int? {
        val input = AskHintInput(gameSessionID)
        try {
            val response =
                apolloClient(userID).mutate(AskHintMutation(input)).toDeferred().await()
            return response.data?.askHint?.hintLeft?.toInt()
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        return null
    }
}