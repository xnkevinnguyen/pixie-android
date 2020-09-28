package com.pixie.android.model.chat

data class MessageData (
    val text: String,
    val belongsToCurrentUser: Boolean,
    val userName: String = ""
)