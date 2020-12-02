package com.pixie.android.data.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatDataSource
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.chat.MessageData
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory
import com.pixie.android.model.profile.UserInfo
import com.pixie.android.model.profile.UserStatistics
import com.pixie.android.model.user.LoginFormState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileRepository( private val dataSource: ProfileDataSource, private val userRepository: UserRepository) {
    private var userProfileInfo = MutableLiveData<UserInfo>()
    private var userProfileStats = MutableLiveData<UserStatistics>()

    private var logList = MutableLiveData<ArrayList<ConnectionHistory>>()
    private var gameList = MutableLiveData<ArrayList<GameHistory>>()

    fun getUserProfile(): LiveData<UserInfo> {
        return userProfileInfo
    }

    fun getUserProfileStats(): LiveData<UserStatistics> {
        return userProfileStats
    }

    fun getLogList(): LiveData<ArrayList<ConnectionHistory>>{
        return logList
    }

    fun getGameList(): LiveData<ArrayList<GameHistory>>{
        return gameList
    }

    fun fetchUserInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val userDataList =
                dataSource.getUserInformation(userRepository.getUser().userId, onReceiveMessage = {
                    CoroutineScope(Dispatchers.Main).launch {

                        userProfileInfo.postValue(it.userInfo)
                        userProfileStats.postValue(it.userStatistics)
                    }
                })
        }
    }

    fun fetchConnectionList(){
        CoroutineScope(Dispatchers.IO).launch {
            val connectionDataList =
                dataSource.getLogInfos(userRepository.getUser().userId, onReceiveMessage = {
                    if (it != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            logList.postValue(ArrayList(it))
                        }
                    }
                })
        }
    }

    fun fetchGameList(){
        CoroutineScope(Dispatchers.IO).launch {
            val gameDataList =
                dataSource.getGamesInfos(userRepository.getUser().userId, onReceiveMessage = {
                    if (it != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            gameList.postValue(ArrayList(it))
                        }
                    }
                })
        }
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: ProfileRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val profileDataSource = ProfileDataSource()
            instance ?: ProfileRepository(profileDataSource, UserRepository.getInstance()).also {
                instance = it
            }
        }
    }
}