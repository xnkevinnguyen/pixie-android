package com.pixie.android.data.game

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GameInvitation
import com.pixie.android.type.AddVirtualPlayerInput
import com.pixie.android.type.GameMode
import com.pixie.android.type.InvitationInput
import com.pixie.android.type.RemoveVirtualPlayerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen

class GameInviteDataSource {

    suspend fun addVirtualPlayer(gameID: Double, userID: Double): Boolean {
        val input = AddVirtualPlayerInput(gameID)
        try {
            val response =
                apolloClient(userID).mutate(AddVirtualPlayerMutation(input)).toDeferred().await()
            return response.data?.addVirtualPlayer != null
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
        return false
    }
    suspend fun removeVirtualPlayer(gameID: Double,playerID:Double, userID: Double): Boolean {
        val input = RemoveVirtualPlayerInput(gameID,playerID)
        try {
            val response =
                apolloClient(userID).mutate(RemoveVirtualPlayerMutation(input)).toDeferred().await()
            return response.data?.removeVirtualPlayer!= null
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
        return false
    }

    suspend fun sendGameInvitation(
        gameID: Double,
        userID: Double,
        receiverID: Double,
        onResult: (RequestResult) -> Unit
    ) {
        val input = InvitationInput(receiverID, gameID)
        try {
            val response =
                apolloClient(userID).mutate(SendInvitationMutation(input)).toDeferred().await()
            if (response.data?.sendInvitation != null) {
                onResult(RequestResult(true))

            } else {
                onResult(RequestResult(false, "Request Error"))
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.toString())

        }
    }

    suspend fun subscribeToGameInvitation(userID: Double,
                                          onNewInvitation:(gameInvitation:GameInvitation)->Unit) {
        apolloClient(userID).subscribe(OnNewInvitationSubscription()).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {
                val data = it.data?.onNewInvitation
                if(data !=null){
                    val sender = ChannelParticipant(data.sender.id,data.sender.username,data.sender.isOnline)
                    onNewInvitation(GameInvitation(sender,data.gameSession.id,data.gameSession.gameHall.id,data.gameSession.gameInfo.mode
                    ,data.gameSession.gameInfo.difficulty,data.gameSession.gameInfo.language))
                }else{
                    Log.d("ApolloException","Error from Game invite subscription.")
                }
            }

    }
}