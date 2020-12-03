package com.pixie.android.data.leaderboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.leaderboard.LeaderboardData
import com.pixie.android.type.GameMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardRepository(private val dataSource: LeaderboardDataSource,
                            private val userRepository: UserRepository) {
    private var bestFreeScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }
    private var bestSoloScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }
    private var bestCoopScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }

    private var bestFreeCumulativeScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }
    private var bestSoloCumulativeScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }
    private var bestCoopCumulativeScoreList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }

    private var mostGameWonList = MutableLiveData<ArrayList<LeaderboardData>>().apply { arrayListOf<LeaderboardData>() }

    fun getBestScore(mode:String): LiveData<ArrayList<LeaderboardData>> {
        if(mode=="Free") return bestFreeScoreList
        else if(mode == "Solo") return bestSoloScoreList
        else return bestCoopScoreList
    }

    fun getBestCumulativeScore(mode:String): LiveData<ArrayList<LeaderboardData>> {
        if(mode=="Free") return bestFreeCumulativeScoreList
        else if(mode == "Solo") return bestSoloCumulativeScoreList
        else return bestCoopCumulativeScoreList
    }

    fun getMostGameWon(): LiveData<ArrayList<LeaderboardData>> {
        return mostGameWonList
    }

    fun fetchBestFreeScore(){
        val mode = GameMode.FREEFORALL
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestFreeScoreList.postValue(it)

                    }
                }
            })
        }
        bestFreeScoreList.value?.sortByDescending { it.value }
    }

    fun fetchBestSoloScore(){
        val mode = GameMode.SOLO
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestSoloScoreList.postValue(it)
                    }
                }
            })
        }
        bestSoloScoreList.value?.sortByDescending { it.value }
    }

    fun fetchBestCoopScore(){
        val mode = GameMode.COOP
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestCoopScoreList.postValue(it)

                    }
                }
            })
        }
        bestCoopScoreList.value?.sortByDescending { it.value }
    }

    fun fetchBestFreeCumulativeScore(){
        val mode = GameMode.FREEFORALL
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestFreeCumulativeScoreList.postValue(it)
                    }
                }
            })
        }
        bestFreeCumulativeScoreList.value?.sortByDescending { it.value }
    }

    fun fetchBestSoloCumulativeScore(){
        val mode = GameMode.SOLO
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestSoloCumulativeScoreList.postValue(it)
                    }
                }
            })
        }
        bestSoloCumulativeScoreList.value?.sortByDescending { it.value }
    }

    fun fetchBestCoopCumulativeScore(){
        val mode = GameMode.COOP
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getBestCumulativeScore(mode, userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        bestCoopCumulativeScoreList.postValue(it)
                    }
                }
            })
        }
        bestCoopCumulativeScoreList.value?.sortByDescending { it.value }
    }

    fun fetchMostGameWon(){
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getMostGameWon( userRepository.getUser().userId, onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        mostGameWonList.postValue(it)
                    }
                }
            })
        }
        mostGameWonList.value?.sortByDescending { it.value }
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