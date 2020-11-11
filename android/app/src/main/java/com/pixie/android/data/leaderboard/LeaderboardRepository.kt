package com.pixie.android.data.leaderboard

import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.type.GameMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestFreeScoreList = it
                    }
                }
            })
        }
        bestFreeScoreList.sortByDescending { it.value }
    }

    fun fetchBestSoloScore(){
        val mode = GameMode.SOLO
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestSoloScoreList = it
                    }
                }
            })
        }
        bestSoloScoreList.sortByDescending { it.value }
    }

    fun fetchBestCoopScore(){
        val mode = GameMode.COOP
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestCoopScoreList = it
                    }
                }
            })
        }
        bestCoopScoreList.sortByDescending { it.value }
    }

    fun fetchBestFreeCumulativeScore(){
        val mode = GameMode.FREEFORALL
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestFreeCumulativeScoreList = it
                    }
                }
            })
        }
        bestFreeCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchBestSoloCumulativeScore(){
        val mode = GameMode.SOLO
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestSoloCumulativeScoreList = it
                    }
                }
            })
        }
        bestSoloCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchBestCoopCumulativeScore(){
        val mode = GameMode.COOP
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestCoopCumulativeScoreList = it
                    }
                }
            })
        }
        bestCoopCumulativeScoreList.sortByDescending { it.value }
    }

    fun fetchMostGameWon(){
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getMostGameWon( userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        mostGameWonList = it
                    }
                }
            })
        }
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