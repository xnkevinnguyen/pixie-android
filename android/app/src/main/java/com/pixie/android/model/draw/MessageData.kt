package com.pixie.android.model.draw

data class MessageData (
    val text: String,
    val belongsToCurrentUser: Boolean,
    val userName: String = ""
)