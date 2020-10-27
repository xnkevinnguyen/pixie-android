package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GameData
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.model.game.PlayersData
import com.pixie.android.type.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking

class GameDataSource() {
    suspend fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language, userId: Double): CreatedGameData? {

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

                val gameChannelData = ChannelData(channelID = data.gameHall.id, channelName = separated[0], participantList = gameParticipant, nParticipant = null, gameID = data.id)
                var playersList = data.gameInfo.players?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                if (playersList == null) {
                    playersList = arrayListOf()
                }
                val gameData = GameData(data.gameInfo.mode, data.gameInfo.language, playersList)
                return CreatedGameData(
                    data.id,
                    gameData,
                    gameChannelData
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
        userId: Double
    ): ArrayList<AvailableGameData> {
        val createAvailableGamesInput = AvailableGamesInput(mode, difficulty, Language.ENGLISH)
        try {
            val response = apolloClient(userId).query(GetAvailableGamesQuery(createAvailableGamesInput))
                .toDeferred().await().data
            val availableGamesQueryData = response?.availableGames
            if (availableGamesQueryData != null) {
                val availableGamesData = ArrayList(availableGamesQueryData.map {

                    var playersList = it.gameInfo.players?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                    if (playersList == null) {
                        playersList = arrayListOf()
                    }
                    val availableGame = GameData(it.gameInfo.mode, it.gameInfo.language, playersList)
                    AvailableGameData(it.id, availableGame)
                })
                return availableGamesData
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
        return arrayListOf()
    }

    suspend fun getAvailableGamesWithoutCriteria(
        userId: Double
    ): ArrayList<AvailableGameData> {
        try {
            val response = apolloClient(userId).query(GetAvailableGamesWithoutCriteriaQuery())
                .toDeferred().await().data
            val availableGamesQueryData = response?.availableGamesWithoutCriteria
            if (availableGamesQueryData != null) {
                val availableGamesData = ArrayList(availableGamesQueryData.map {

                    var playersList = it.gameInfo.players?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                    if (playersList == null) {
                        playersList = arrayListOf()
                    }
                    val availableGame = GameData(it.gameInfo.mode, it.gameInfo.language, playersList)
                    AvailableGameData(it.id, availableGame)
                })
                return availableGamesData
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
        return arrayListOf()
    }

    suspend fun subscribeToAvailableGameChange(
        userID: Double,
        mode:GameMode,
        difficulty: GameDifficulty,
        onAvailableGameSessionsChange: (ArrayList<AvailableGameData>?) -> Unit
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

                        var gamePlayers = it.gameInfo.players?.map {
                            ChannelParticipant(it.id, it.username, it.isOnline)
                        }
                        if (gamePlayers == null) {
                            gamePlayers = arrayListOf()
                        }

                        val gameInfo = GameData(it.gameInfo.mode, it.gameInfo.language, gamePlayers)
                        AvailableGameData(it.id, gameInfo)
                    })
                    onAvailableGameSessionsChange(availableGamesData)
                } else {
                    Log.d("ApolloException", "Missing attributes from request")
                }
            }
    }

    suspend fun enterGame(gameID: Double, userId: Double): CreatedGameData? {
        val enterGameInput = GameSessionInput(gameID)
        try {
            val response =
                apolloClient(userId).mutate(EnterGameMutation(enterGameInput)).toDeferred()
                    .await()
            val data = response.data?.enterGame
            if (data != null) {

                var gameParticipant = data.gameHall.participants?.map {
                    ChannelParticipant(it.id, it.username, it.isOnline)
                }
                if (gameParticipant == null) {
                    gameParticipant = arrayListOf()
                }
                val separated: List<String> = data.gameHall.name.split("-")

                val gameChannelData = ChannelData(channelID = data.gameHall.id, channelName = separated[0], participantList = gameParticipant, nParticipant = null, gameID = data.id)
                var playersList = data.gameInfo.players?.map { ChannelParticipant(it.id, it.username, it.isOnline) }
                if (playersList == null) {
                    playersList = arrayListOf()
                }
                val gameData = GameData(data.gameInfo.mode, data.gameInfo.language, playersList)
                return CreatedGameData(
                    data.id,
                    gameData,
                    gameChannelData
                )


            } else {
                Log.d("ApolloException", "Missing attributes from request")
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())
        }
        return null
    }

}