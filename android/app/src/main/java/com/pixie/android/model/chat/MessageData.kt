package com.pixie.android.model.chat

data class MessageData (
    val text: String,
    var belongsToCurrentUser: Boolean?,
    val userName: String = "",
    val timePosted: String = ""
)