package com.pixie.android.model.game

import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language

data class GameData(val mode: GameMode, val language: Language, var listPlayers: List<ChannelParticipant>?)