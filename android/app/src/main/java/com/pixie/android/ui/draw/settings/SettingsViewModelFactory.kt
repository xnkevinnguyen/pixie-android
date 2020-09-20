package com.pixie.android.ui.draw.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.ui.draw.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel() as T
    }
}