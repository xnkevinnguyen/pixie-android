package com.pixie.android.ui.draw.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.history.ConnectionHistory
import com.pixie.android.model.history.GameHistory
import com.pixie.android.model.profile.UserInfo
import com.pixie.android.model.profile.UserStatistics
import com.pixie.android.model.user.AvatarColorData
import com.pixie.android.model.user.LoggedInUser
import kotlinx.coroutines.runBlocking

class ProfileViewModel(private val userRepository: UserRepository, private val profileRepository: ProfileRepository): ViewModel() {
    fun logout() {
        userRepository.logout()
    }

    fun fetchAllInfo(){
        profileRepository.fetchUserInfo()
        profileRepository.fetchConnectionList()
        profileRepository.fetchGameList()
    }

    fun getUserInfo(): LiveData<UserInfo> = profileRepository.getUserProfile()

    fun getUserStats():LiveData<UserStatistics> = profileRepository.getUserProfileStats()

    fun getLogList():LiveData<ArrayList<ConnectionHistory>> = profileRepository.getLogList()

    fun getGameList():LiveData<ArrayList<GameHistory>> = profileRepository.getGameList()

    fun getUsername(): String = userRepository.getUser().username
    
    fun getAvatarColor():AvatarColorData = userRepository.getAvatarColor()
}