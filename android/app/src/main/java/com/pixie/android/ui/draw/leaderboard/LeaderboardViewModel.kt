package com.pixie.android.ui.draw.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.leaderboard.LeaderboardRepository
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.leaderboard.LeaderboardData

class LeaderboardViewModel(private val leaderboardRepository: LeaderboardRepository): ViewModel() {

    fun setup(){
        leaderboardRepository.fetchBestFreeScore()
        leaderboardRepository.fetchBestSoloScore()
        leaderboardRepository.fetchBestCoopScore()
        leaderboardRepository.fetchBestFreeCumulativeScore()
        leaderboardRepository.fetchBestSoloCumulativeScore()
        leaderboardRepository.fetchBestCoopCumulativeScore()
        leaderboardRepository.fetchMostGameWon()
    }

    fun getBestScore(mode: String): ArrayList<LeaderboardData>{
        return leaderboardRepository.getBestScore(mode)
    }

    fun getBestCumulativeScore(mode: String): ArrayList<LeaderboardData>{
        return leaderboardRepository.getBestCumulativeScore(mode)
    }

    fun getMostGameWon(): ArrayList<LeaderboardData>{
        return leaderboardRepository.getMostGameWon()
    }
}