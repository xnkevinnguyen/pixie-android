package com.pixie.android.model.game

import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.type.GameMode

data class GameInvitation (
    val sender:ChannelParticipant,
    val gameID:Double,
    val channelID:Double,
    val gameMode: GameMode
)