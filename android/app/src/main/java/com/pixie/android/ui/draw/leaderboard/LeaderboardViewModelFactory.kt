package com.pixie.android.ui.draw.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.leaderboard.LeaderboardRepository
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.profile.ProfileViewModel

class LeaderboardViewModelFactory(private val leaderboardRepository: LeaderboardRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LeaderboardViewModel(leaderboardRepository) as T
    }
}