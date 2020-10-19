package com.pixie.android.model.game

import com.pixie.android.model.chat.ChannelParticipant

data class AvailableGame(val mode: String, val language: String, val listPlayers: ArrayList<ChannelParticipant>)