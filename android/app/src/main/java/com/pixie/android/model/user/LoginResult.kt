package com.pixie.android.model.user

data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
)