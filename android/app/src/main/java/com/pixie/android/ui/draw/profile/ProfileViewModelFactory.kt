package com.pixie.android.ui.draw.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.user.UserRepository

@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(private val userRepository: UserRepository, private val profileRepository: ProfileRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(userRepository, profileRepository) as T
    }
}