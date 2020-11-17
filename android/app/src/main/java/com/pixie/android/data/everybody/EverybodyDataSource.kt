package com.pixie.android.data.everybody

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.GetFriendListQuery
import com.pixie.android.GetUsersQuery
import com.pixie.android.apolloClient
import com.pixie.android.model.chat.ChannelParticipant

class EverybodyDataSource {

    suspend fun getAllUsers(
        userId: Double
        //onReceiveMessage: (ArrayList<ChannelParticipant>?) -> Unit
    ): ArrayList<ChannelParticipant>  {
        try {
            val response = apolloClient(userId).query(GetUsersQuery())
                .toDeferred().await().data
            val everyUserQueryData = response?.users
            if (everyUserQueryData != null) {

                val userList = ArrayList(everyUserQueryData.map {

                    ChannelParticipant(it.id, it.username, it.isOnline, it.isVirtual)
                })
                //onReceiveMessage(friendList)
                return userList
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException", "Error fetching available games")

        return arrayListOf()
    }
}