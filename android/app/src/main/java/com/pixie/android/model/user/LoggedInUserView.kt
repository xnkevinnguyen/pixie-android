package com.pixie.android.model.user

import com.pixie.android.type.Language

data class LoggedInUserView(
        val username: String,
        val userID:Double,
        val theme:String?=null,
        val language:Language?=null
)