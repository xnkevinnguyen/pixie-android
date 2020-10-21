package com.pixie.android.model.game

import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language

data class AvailableGame(val mode: GameMode, val language: Language, val listPlayers: List<ChannelParticipant>?, val channel: ChannelData)