package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.CreateGameMutation
import com.pixie.android.ExitChannelMutation
import com.pixie.android.ExitGameMutation
import com.pixie.android.apolloClient
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GameData
import com.pixie.android.type.*

class GameDataSource() {
    suspend fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language, userId: Double): GameData? {

        val createGameInput = CreateGameInput(mode, difficulty , language)
        try {
            val response =
                apolloClient(userId).mutate(CreateGameMutation(createGameInput)).toDeferred()
                    .await()
            val data = response.data?.createGame
            if (data != null) {

                var gameParticipant = data.gameHall.participants?.map {
                    ChannelParticipant(it.id, it.username, it.isOnline)
                }
                if (gameParticipant == null) {
                    gameParticipant = arrayListOf()
                }
                val separated: List<String> = data.gameHall.name.split("-")

                var channelData = ChannelData(channelID = data.gameHall.id, channelName = separated[0], participantList = gameParticipant, nParticipant = null, gameID = data.id)
                return GameData(
                    data.id,
                    channelData
                )


            } else {
                Log.d("ApolloException", "CreateChannel Error")
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())
        }
        return null
    }

    suspend fun exitGame(gameID: Double, userId: Double) {
        val exitGameInput = GameSessionInput(gameID)
        try {
            apolloClient(userId).mutate(ExitGameMutation(exitGameInput)).toDeferred().await()

        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
    }

}