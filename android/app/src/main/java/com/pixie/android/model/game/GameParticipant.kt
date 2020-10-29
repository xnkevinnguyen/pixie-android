package com.pixie.android.model.game

data class GameParticipant(val id:Double, val username:String, val isOnline:Boolean?, var score: Double=0.0)