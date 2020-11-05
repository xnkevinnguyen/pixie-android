package com.pixie.android.ui.draw.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.profile.ProfileViewModel

class LeaderboardViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LeaderboardViewModel() as T
    }
}