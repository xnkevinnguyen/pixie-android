package com.pixie.android.model.chat

import com.pixie.android.OnChannelChangeSubscription

data class ChannelData (val channelID:Double, val channelName:String,val participantList:List<ChannelParticipant>)