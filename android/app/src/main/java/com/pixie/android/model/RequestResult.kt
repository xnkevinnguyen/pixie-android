package com.pixie.android.model

data class RequestResult (
    val isSuccess:Boolean,
    val error: String? = null
)