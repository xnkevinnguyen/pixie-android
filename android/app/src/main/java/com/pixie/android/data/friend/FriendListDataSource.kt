package com.pixie.android.data.friend

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.type.*

class FriendListDataSource {

    suspend fun getFriendList(
        userId: Double,
        onReceiveMessage: (ArrayList<ChannelParticipant>?) -> Unit
    )  {
        try {
            val response = apolloClient(userId).query(GetFriendListQuery())
                .toDeferred().await().data
            val friendListQueryData = response?.me?.friends
            //Log.d("here", "here data source $friendListQueryData")
            if (friendListQueryData != null) {

                val friendList = ArrayList(friendListQueryData.map {

                    ChannelParticipant(it.id, it.username, it.isOnline)
                })
                onReceiveMessage(friendList)
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")
    }

    suspend fun addFriend(friendId: Double, userId: Double): Boolean {
        val createFriendInput = FriendInput(friendId)
        try {
            val response =
                apolloClient(userId).mutate(AddFriendMutation(createFriendInput)).toDeferred().await()
            return response.data?.addFriend != null
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
        return false
    }

    suspend fun removeFriend(friendId: Double, userId: Double): Boolean {
        val createFriendInput = FriendInput(friendId)
        try {
            val response =
                apolloClient(userId).mutate(RemoveFriendMutation(createFriendInput)).toDeferred().await()
            return response.data?.removeFriend != null
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
        return false
    }

}