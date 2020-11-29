package com.pixie.android.model.game

import com.pixie.android.type.GameMode
import com.pixie.android.type.GameState
import com.pixie.android.type.GameStatus

data class GameSessionData (
    var id: Double,
    var currentDrawerId:Double?,
    var currentWord:String,
    var currentRound:Double,
    var guessesLeft:Double?=3.0,
    var status: GameStatus,
    var channelID:Double,
    var players:ArrayList<GameParticipant>,
    var mode: GameMode,
    var state:GameState?,
    var hintsLeft:Int?=null,
    var winners:ArrayList<GameParticipant>? = null

)