package com.pixie.android.model.history

import com.pixie.android.model.profile.ScoreData

data class GameHistory (val date: String, val points: List<ScoreData>, val winner: List<String>?,
                        val score: Double?, val difficulty: String, val gameMode: String)