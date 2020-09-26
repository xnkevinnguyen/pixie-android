package com.pixie.android.data.chat

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.OnChannelChangeSubscription
import com.pixie.android.OnNewMessageSubscription
import com.pixie.android.SendMessageMutation
import com.pixie.android.apolloClient
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import com.pixie.android.type.AddMessageInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.retryWhen

class ChatDataSource() {

    suspend fun sendMessageToChannel(messageData: MessageData,channelID: Double,userId:Double):SendMessageMutation.Data?{
        val addMessageInput = AddMessageInput(messageData.text,channelID,userId )
        var response : SendMessageMutation.Data? =null
        try{
            response =  apolloClient.mutate(SendMessageMutation(addMessageInput)).toDeferred().await().data

        }catch(e: ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return response
    }

    suspend fun suscribeToChannelMessages(channelID:Double, onReceiveMessage:(messageData:MessageData)->Unit){
        apolloClient.subscribe(OnNewMessageSubscription(channelID)).
        toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {

                val messageContent =it.data?.onNewMessage?.content
                val messageSenderUsername = it.data?.onNewMessage?.sender?.username


                if( messageContent !=null && messageSenderUsername !=null){
                    val messageData = MessageData(messageContent,false,messageSenderUsername)
                    onReceiveMessage(messageData)
                }
            }
    }

    suspend fun suscribeToChannelChange(channelID:Double, onChannelChange:(channelData:ChannelData)->Unit){
        apolloClient.subscribe(OnChannelChangeSubscription(channelID)).
        toFlow()
            .retryWhen { _, attempt ->
                delay(attempt * 1000)
                true
            }
            .collect {

                val data = it.data?.onChannelChange
                if(data != null){

                    val channelParticipant = data.participants.map{
                        ChannelParticipant(it.id,it.username,it.isOnline)
                    }
                    val channelData = ChannelData(
                        data.id,
                        data.name,
                       channelParticipant
                    )
                    onChannelChange(channelData)

                }else{
                    Log.d("ApolloException","Missing attributes from request")
                }

            }
    }
    
}