package com.pixie.android.ui.draw.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(userRepository) as T
    }
}