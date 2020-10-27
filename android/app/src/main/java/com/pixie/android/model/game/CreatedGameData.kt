package com.pixie.android.model.game

import com.pixie.android.model.chat.ChannelData

data class CreatedGameData(val gameId:Double, val gameData: GameData, var gameChannelData: ChannelData)