package com.pixie.android.model.chat

data class ChannelData(
    val channelID: Double, val channelName: String,
    var participantList: List<ChannelParticipant>?,
    var unreadMessages:Int=0,
    var nParticipant:Int?, var gameID:Double?=null)

