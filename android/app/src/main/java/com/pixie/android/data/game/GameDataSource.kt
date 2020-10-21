package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGame
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameData
import com.pixie.android.model.profile.User
import com.pixie.android.model.profile.UserInfo
import com.pixie.android.model.profile.UserStatistics
import com.pixie.android.type.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen

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

    suspend fun getAvailableGames(
        mode: GameMode,
        difficulty: GameDifficulty,
        userId: Double,
        onReceiveMessage: (ArrayList<AvailableGameData>?) -> Unit
    ) {
        val createAvailableGamesInput = AvailableGamesInput(mode, difficulty)
        try {
            val response = apolloClient(userId).query(GetAvailableGamesQuery(createAvailableGamesInput))
                .toDeferred().await().data
            val availableGamesQueryData = response?.availableGames
            if (availableGamesQueryData != null) {
                val availableGamesData = ArrayList(availableGamesQueryData.map {
                    var participantList = it.gameHall.participants?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                    if (participantList == null) {
                        participantList = arrayListOf()
                    }
                    val channelData = ChannelData(it.gameHall.id, it.gameHall.name, participantList, nParticipant = null)

                    var playersList = it.gameInfo.players?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                    if (playersList == null) {
                        playersList = arrayListOf()
                    }
                    val availableGame = AvailableGame(it.gameInfo.mode, it.gameInfo.language, playersList, channelData)
                    AvailableGameData(it.id, availableGame)
                })

                onReceiveMessage(availableGamesData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching user channels")
    }

    suspend fun subscribeToAvailableGameChange(
        userID: Double,
        mode:GameMode,
        difficulty: GameDifficulty,
        onChannelChange: (ArrayList<AvailableGameData>?) -> Unit
    ) {
        apolloClient(userID).subscribe(OnAvailableGameSessionsChangeSubscription(mode, difficulty)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {

                val data = it.data?.onAvailableGameSessionsChange
                if (data != null) {

                    val availableGamesData = ArrayList(data.map {

                        var gameChannelParticipant = it.gameHall.participants?.map {
                            ChannelParticipant(it.id, it.username, it.isOnline)
                        }
                        if (gameChannelParticipant == null) {
                            gameChannelParticipant = arrayListOf()
                        }

                        var gamePlayers = it.gameInfo.players?.map {
                            ChannelParticipant(it.id, it.username, it.isOnline)
                        }
                        if (gamePlayers == null) {
                            gamePlayers = arrayListOf()
                        }
                        val channelData = ChannelData(
                            it.gameHall.id,
                            it.gameHall.name,
                            gameChannelParticipant, nParticipant = null
                        )

                        val gameInfo = AvailableGame(it.gameInfo.mode, it.gameInfo.language, gamePlayers, channelData)
                        AvailableGameData(it.id, gameInfo)
                    })
                    onChannelChange(availableGamesData)
                } else {
                    Log.d("ApolloException", "Missing attributes from request")
                }
            }
    }

}