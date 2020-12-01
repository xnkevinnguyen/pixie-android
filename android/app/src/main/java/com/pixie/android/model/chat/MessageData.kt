package com.pixie.android.model.chat

data class ChannelMessageObject(
    var messageList: ArrayList<MessageData>,
    var isHistoryLoaded:Boolean = false
)
data class MessageData (
    val text: String,
    var belongsToCurrentUser: Boolean?,
    val userName: String = "",
    val timePosted: String = "",
    var shouldBeHidden:Boolean = false,
    var isFromHost:Boolean = false
)