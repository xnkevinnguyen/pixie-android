package com.pixie.android.data.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.type.GameMode
import kotlinx.coroutines.runBlocking

class LeaderboardRepository(private val dataSource: LeaderboardDataSource,
                            private val userRepository: UserRepository) {
    private var bestFreeScoreList = ArrayList<LeaderboardData>()
    private var bestSoloScoreList = ArrayList<LeaderboardData>()
    private var bestCoopScoreList = ArrayList<LeaderboardData>()

    private var bestFreeCumulativeScoreList = ArrayList<LeaderboardData>()
    private var bestSoloCumulativeScoreList = ArrayList<LeaderboardData>()
    private var bestCoopCumulativeScoreList = ArrayList<LeaderboardData>()

    private var mostGameWonList = ArrayList<LeaderboardData>()

    fun getBestScore(mode:String): ArrayList<LeaderboardData>{
        if(mode=="Free") return bestFreeScoreList
        else if(mode == "Solo") return bestSoloScoreList
        else return bestCoopScoreList
    }

    fun getBestCumulativeScore(mode:String): ArrayList<LeaderboardData> {
        if(mode=="Free") return bestFreeCumulativeScoreList
        else if(mode == "Solo") return bestSoloCumulativeScoreList
        else return bestCoopCumulativeScoreList
    }

    fun getMostGameWon(): ArrayList<LeaderboardData> {
        return mostGameWonList
    }

    fun fetchBestFreeScore(){
        val mode = GameMode.FREEFORALL
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestScore(mode, userRepository.getUser().userId)
        }
        bestFreeScoreList = bestScores
        bestFreeScoreList.sortByDescending { it.value }
    }

    fun fetchBestSoloScore(){
        val mode = GameMode.SOLO
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestScore(mode, userRepository.getUser().userId)
        }
        bestSoloScoreList = bestScores
        bestSoloScoreList.sortByDescending { it.value }
    }

    fun fetchBestCoopScore(){
        val mode = GameMode.COOP
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestScore(mode, userRepository.getUser().userId)
        }
        bestCoopScoreList = bestScores
        bestCoopScoreList.sortByDescending { it.value }
    }

    fun fetchBestFreeCumulativeScore(){
        val mode = GameMode.FREEFORALL
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId)
        }
        bestFreeCumulativeScoreList = bestScores
        bestFreeCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchBestSoloCumulativeScore(){
        val mode = GameMode.SOLO
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId)
        }
        bestSoloCumulativeScoreList = bestScores
        bestSoloCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchBestCoopCumulativeScore(){
        val mode = GameMode.COOP
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId)
        }
        bestCoopCumulativeScoreList = bestScores
        bestCoopCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchMostGameWon(){
        var bestScores: ArrayList<LeaderboardData>
        runBlocking {
            bestScores = dataSource.getMostGameWon(userRepository.getUser().userId)
        }
        mostGameWonList = bestScores
        mostGameWonList.sortByDescending { it.value }
    }
    // Singleton
    companion object {
        @Volatile
        private var instance: LeaderboardRepository? = null
        private var dataSource: LeaderboardDataSource = LeaderboardDataSource()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LeaderboardRepository(
                dataSource, UserRepository.getInstance()
            ).also {
                instance = it
            }
        }
    }
}