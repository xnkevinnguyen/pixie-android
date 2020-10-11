package com.pixie.android.data.chat

import android.util.Log
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.request.RequestHeaders
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import com.pixie.android.type.AddMessageInput
import com.pixie.android.type.EnterChannelInput
import com.pixie.android.type.ExitChannelInput
import com.pixie.android.type.UserChannelInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen
import java.util.*

class ChatDataSource() {
    suspend fun getUserChannels(
        userId:Double
    ): List<ChannelData>?{
        val userChannelInput = UserChannelInput(userId)
        try{
            val response = apolloClient(userId).query(GetUserChannelsQuery(userChannelInput)).requestHeaders(
                RequestHeaders.builder()
                    .addHeader("authToken", "1")
                    .build()
            )
                .toDeferred().await().data
            val channelQueryData = response?.userChannels?.channels
            if (channelQueryData != null){
                val channelData =channelQueryData.map {
                    val participantList = it.participants.map{ ChannelParticipant(it.id,it.username,it.isOnline) }
                    ChannelData(it.id,it.name,participantList)
                }
                return channelData
            }
        }catch(e:ApolloException){
            Log.d("ApolloException", e.message.toString())

        }
        Log.d("ApolloException","Error fetching user channels")
        return null
    }

    suspend fun sendMessageToChannel(
        message: String,
        channelID: Double,
        userId: Double
    ): SendMessageMutation.Data? {
        val addMessageInput = AddMessageInput(message, channelID, userId)
        try {
            return apolloClient(userId).mutate(SendMessageMutation(addMessageInput)).toDeferred()
                .await().data

        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())
        }
        return null
    }

    suspend fun enterChannel(channelID: Double, userId: Double): ChannelData? {
        val enterChannelInput = EnterChannelInput(channelID, userId)
        try {
            val response =
                apolloClient(userId).mutate(EnterChannelMutation(enterChannelInput)).toDeferred().await()
            val data = response.data?.enterChannel
            if (data != null) {

                val channelParticipant = data.participants.map {
                    ChannelParticipant(it.id, it.username, it.isOnline)
                }
                return ChannelData(
                    data.id,
                    data.name,
                    channelParticipant
                )


            } else {
                Log.d("ApolloException", "Missing attributes from request")
            }
        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())
        }
        return null
    }

    suspend fun exitChannel(channelID: Double, userId: Double) {
        val exitChannelInput = ExitChannelInput(channelID, userId)
        try {
            apolloClient(userId).mutate(ExitChannelMutation(exitChannelInput)).toDeferred().await()


        } catch (e: ApolloException) {
            Log.d("ApolloException", e.message.toString())

        }
    }

    suspend fun suscribeToChannelMessages(
        userID:Double,
        channelID: Double,
        onReceiveMessage: (messageData: MessageData) -> Unit){
         apolloClient(userID).subscribe(OnNewMessageSubscription(channelID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {

                val messageContent = it.data?.onNewMessage?.content
                val messageSenderUsername = it.data?.onNewMessage?.sender?.username
                val messageTimePosted = it.data?.onNewMessage?.postedAt


                if (messageContent != null && messageSenderUsername != null && messageTimePosted != null) {
                    val messageData = MessageData(messageContent, null, messageSenderUsername, messageTimePosted)
                    onReceiveMessage(messageData)
                }
            }

    }

    suspend fun suscribeToChannelChange(
        userID: Double,
        channelID: Double,
        onChannelChange: (channelData: ChannelData) -> Unit
    ) {
         apolloClient(userID).subscribe(OnChannelChangeSubscription(channelID)).toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {

                val data = it.data?.onChannelChange
                if (data != null) {

                    val channelParticipant = data.participants.map {
                        ChannelParticipant(it.id, it.username, it.isOnline)
                    }
                    val channelData = ChannelData(
                        data.id,
                        data.name,
                        channelParticipant
                    )
                    onChannelChange(channelData)

                } else {
                    Log.d("ApolloException", "Missing attributes from request")
                }

            }
    }

}