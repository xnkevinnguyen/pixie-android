package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.AddVirtualPlayerMutation
import com.pixie.android.apolloClient
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.type.AddVirtualPlayerInput

class GameInviteDataSource{

    suspend fun addVirtualPlayer(gameID:Double,userID:Double):Boolean{
        val input = AddVirtualPlayerInput(gameID)
        try{
            val response = apolloClient(userID).mutate(AddVirtualPlayerMutation(input)).toDeferred().await()
            return response.data?.addVirtualPlayer!=null
            Log.d("AddVirtualPlayerMutation",response.toString())
        }catch(e:ApolloException){
            Log.d("ApolloException", e.toString())

        }
        return false
    }
}