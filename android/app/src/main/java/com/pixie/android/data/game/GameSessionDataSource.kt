package com.pixie.android.data.game

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.CommandType
import com.pixie.android.model.draw.PathPoint
import com.pixie.android.model.game.GameParticipant
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.GuessWordInput
import com.pixie.android.type.StartGameInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen

class GameSessionDataSource {
    suspend fun startGame( gameID: Double,userID: Double):GameSessionData?{
        val startGameInput = StartGameInput(gameID)
        try{
            val response = apolloClient(userID).mutate(StartGameMutation(startGameInput)).toDeferred()
                .await()
            val data = response.data?.startGame
            if(data!=null && data.gameInfo.players !=null){
                val players = ArrayList(data.gameInfo.players.map {
                    GameParticipant(it.id,it.username,it.isOnline)
                })
                return GameSessionData(data.id, data.currentDrawerId, data.currentWord,
                data.currentRound,3.0-data.sprintTries!!,data.status,data.gameHall.id,players, data.gameInfo.mode)
            }

        }catch (e: ApolloException){
            Log.d("ApolloException", e.toString())

        }
        return null
    }

    suspend fun suscribeToTimer(
        gameSessionID:Double,
        userID: Double,
        onTimerChange:(Double)->Unit
    ){
        apolloClient(userID).subscribe(OnTimerChangeSubscription(gameSessionID)).toFlow()
            .retryWhen{ _, attempt->
                delay(attempt * 1000)
                true
            }.collect {
                 val timerDataChange = it.data?.onTimerChange
                if(timerDataChange !=null){
                    onTimerChange(timerDataChange.currentValue)
                }
            }
    }
    suspend fun subscribeToGameSessionChange(
        gameSessionID:Double,
        userID: Double,
        onGameSessionChange:(GameSessionData)->Unit
    ){
        apolloClient(userID).subscribe(OnGameSessionChangeSubscription(gameSessionID)).toFlow()
            .retryWhen{ _, attempt->
                delay(attempt * 1000)
                true
            }.collect {

                val data = it.data?.onGameSessionChange
                if(data !=null && data.gameInfo.players !=null){
                    val players = ArrayList(data.gameInfo.players.map {
                        GameParticipant(it.id,it.username,it.isOnline)
                    })
                    data.gameInfo.scores?.forEach { score->
                        players.forEach {gameParticipant->
                            if(score.id == gameParticipant.id && score.value !=null){
                                gameParticipant.score=score.value
                            }
                        }
                    }
                    val gameSession = GameSessionData(
                        data.id,
                        data.currentDrawerId,
                        data.currentWord,
                        data.currentRound,
                        3.0-data.sprintTries!!,
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
        onPathChange:(CanvasCommand)->Unit
    ){

        apolloClient(userID).subscribe(OnPathChangeSubscription(gameSessionID)).toFlow()
            .retryWhen{ _, attempt->
                delay(attempt * 1000)
                true
            }.collect {
                val data = it.data?.onPathChange
                if(data!=null){
                    val pathList = arrayListOf<PathPoint>()
                    val length = data.points.size
                    for (i in 0..length){
                        if(i+1<length){
                            val point=PathPoint(data.points[i].x!!.toFloat(),
                            data.points[i].y!!.toFloat(),
                            data.points[i+1].x!!.toFloat(),
                            data.points[i+1].y!!.toFloat())
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
                    val command = CanvasCommand(CommandType.DRAW,paint,pathList)
                    onPathChange(command)
                }

            }
    }
    suspend fun guessWord(
        word:String,
        gameSessionID: Double,
        userID: Double
        ):Boolean{
        val guessWordInput = GuessWordInput(word,gameSessionID)
        try {
            val response = apolloClient(userID).mutate(GuessWordMutation(guessWordInput)).toDeferred().await()
            if(response.data?.guessWord!=null){
                return true
            }
        }catch(e:ApolloException){
            Log.d("ApolloException", e.message.toString())

        }
        return false
    }
}