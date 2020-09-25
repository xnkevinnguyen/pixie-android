package com.pixie.android.model.user

data class AuthResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
)