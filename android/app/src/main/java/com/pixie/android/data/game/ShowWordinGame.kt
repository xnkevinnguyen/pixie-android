package com.pixie.android.data.game

enum class ShowWordinGameType{
    IS_DRAWER,
    OTHER_DRAWER,
    ANSWER,
    END,
    NONE
}

data class ShowWordinGame (val shouldShowWordBig:Boolean, val type:ShowWordinGameType,val word:String?=null)
