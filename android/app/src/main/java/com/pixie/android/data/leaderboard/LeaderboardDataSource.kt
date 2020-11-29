package com.pixie.android.data.leaderboard

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.GetBestCumulativeScoreQuery
import com.pixie.android.GetBestScoreQuery
import com.pixie.android.GetMostWonGamesQuery
import com.pixie.android.apolloClient
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.type.*

class LeaderboardDataSource {
    suspend fun getBestScore(
        mode: GameMode,
        userId: Double,
        onReceiveMessage: (ArrayList<LeaderboardData>?) -> Unit
    )  {
        val createLeaderboardInput = LeaderBoardInput(mode)
        try {
            val response = apolloClient(userId).query(GetBestScoreQuery(createLeaderboardInput))
                .toDeferred().await().data
            val bestScoreQueryData = response?.leaderboardBestScore
            if (bestScoreQueryData != null) {
                val bestScoreData = ArrayList(bestScoreQueryData.map {

                    LeaderboardData(it.username, it.value, it.avatarForeground, it.avatarBackground)
                })
                onReceiveMessage(bestScoreData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
    }

    suspend fun getBestCumulativeScore(
        mode: GameMode,
        userId: Double,
        onReceiveMessage: (ArrayList<LeaderboardData>?) -> Unit
    ) {
        val createLeaderboardInput = LeaderBoardInput(mode)
        try {
            val response = apolloClient(userId).query(GetBestCumulativeScoreQuery(createLeaderboardInput))
                .toDeferred().await().data
            val bestCumulativeScoreQueryData = response?.leaderboardCumulativeScore
            if (bestCumulativeScoreQueryData != null) {
                val bestCumulativeScoreData = ArrayList(bestCumulativeScoreQueryData.map {

                    LeaderboardData(it.username, it.value, it.avatarForeground, it.avatarBackground)
                })
                onReceiveMessage(bestCumulativeScoreData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
    }

    suspend fun getMostGameWon(
        userId: Double,
        onReceiveMessage: (ArrayList<LeaderboardData>?) -> Unit
    ){
        try {
            val response = apolloClient(userId).query(GetMostWonGamesQuery())
                .toDeferred().await().data
            val mostGameWonQueryData = response?.leaderboardMostWonGames
            if (mostGameWonQueryData != null) {
                val mostGameWonData = ArrayList(mostGameWonQueryData.map {

                    LeaderboardData(it.username, it.value, it.avatarForeground, it.avatarBackground)
                })
                onReceiveMessage(mostGameWonData)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
    }
}