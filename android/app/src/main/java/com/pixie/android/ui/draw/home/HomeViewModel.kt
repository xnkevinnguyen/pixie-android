package com.pixie.android.ui.draw.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.user.UserRepository

class HomeViewModel(private val userRepository: UserRepository): ViewModel() {
    fun logout() = userRepository.logout()
}