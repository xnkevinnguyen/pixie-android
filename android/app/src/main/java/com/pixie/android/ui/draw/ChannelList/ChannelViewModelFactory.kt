package com.pixie.android.ui.draw.ChannelList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.ui.draw.settings.SettingsViewModel

@Suppress("UNCHECKED_CAST")
class ChannelViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChannelViewModel() as T
    }
}