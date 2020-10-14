package com.pixie.android.data.profile

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory
import com.pixie.android.model.profile.ScoreData
import com.pixie.android.model.profile.User
import com.pixie.android.model.profile.UserInfo
import com.pixie.android.model.profile.UserStatistics

class ProfileDataSource {

    suspend fun getUserInformation(
        userId: Double,
        onReceiveMessage: (User) -> Unit
    ) {
        try {
            val response = apolloClient(userId).query(GetUserInformationsQuery())
                .toDeferred().await().data
            val userQueryData = response?.me
            if (userQueryData != null) {
                val userInfo = UserInfo(username = userQueryData.username, firstName = userQueryData.firstName, lastName = userQueryData.lastName,
                avatar = userQueryData.avatar, createdAt = userQueryData.createdAt)
                val userStatistics = UserStatistics(nbGames = userQueryData.nGames, percentWin = userQueryData.winPercent, averageGameTime = userQueryData.playTimeAverage,
                totalGameTime = userQueryData.totalGameTime, bestFreeScore = userQueryData.bestFreeForAllScore, bestSoloScore = userQueryData.bestSprintSoloScore,
                bestCoopScore = userQueryData.bestSprintCoopScore)

                val user = User(userInfo = userInfo, userStatistics = userStatistics)
                onReceiveMessage(user)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching user channels")
    }

    suspend fun getLogInfos(
        userId: Double,
        onReceiveMessage: (ArrayList<ConnectionHistory>?) -> Unit
    ) {
        try {
            val response = apolloClient(userId).query(GetConnectionQuery())
                .toDeferred().await().data
            val logQueryData = response?.logInfos
            if (logQueryData != null) {
                val logData = ArrayList(logQueryData.map {
                    ConnectionHistory(connection = it.loginDate.toString(), disconnection = it.logOutDate.toString())
                })
                onReceiveMessage(logData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching user channels")
    }

    suspend fun getGamesInfos(
        userId: Double,
        onReceiveMessage: (ArrayList<GameHistory>?) -> Unit
    ) {
        try {
            val response = apolloClient(userId).query(GetGamesQuery())
                .toDeferred().await().data
            val gameQueryData = response?.me?.playedGames
            if (gameQueryData != null) {
                val gameData = ArrayList(gameQueryData.map {
                    var scoreList =
                        it.scores?.map { ScoreData(it.user.username, it.tries, it.wordGuessed, it.value) }
                    if (scoreList == null) {
                        scoreList = arrayListOf()
                    }
                    GameHistory(date = it.createdAt.toString(), points = scoreList, winner = it.winner?.username, score = it.bestScore,
                    difficulty = it.difficulty.rawValue, gameMode = it.mode.rawValue)
                })
                onReceiveMessage(gameData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching user channels")
    }
}