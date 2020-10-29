package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelParticipant
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
                    ChannelParticipant(it.id,it.username,it.isOnline)
                })
                return GameSessionData(data.id, data.currentDrawerId, data.currentWord,
                data.currentRound,data.status,data.gameHall.id,players, data.gameInfo.mode)
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
                        ChannelParticipant(it.id,it.username,it.isOnline)
                    })
                    val gameSession = GameSessionData(
                        data.id,
                        data.currentDrawerId,
                        data.currentWord,
                        data.currentRound,
                        data.status,
                        data.gameHall.id,
                        players,
                        data.gameInfo.mode
                    )
                    onGameSessionChange(gameSession)
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