package com.pixie.android.model.login

data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
)