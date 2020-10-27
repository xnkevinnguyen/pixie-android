package com.pixie.android.model.game

import com.pixie.android.data.game.GameID
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.type.GameStatus

data class GameSessionData (
    var id: GameID,
    var currentDrawerId:Double?,
    var currentWord:String,
    var currentRound:Double,
    var status: GameStatus,
    var channelID:Double,
    var players:ArrayList<ChannelParticipant>

)