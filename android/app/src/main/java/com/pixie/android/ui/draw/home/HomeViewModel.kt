package com.pixie.android.ui.draw.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.user.UserRepository
import kotlinx.coroutines.runBlocking

class HomeViewModel(private val userRepository: UserRepository): ViewModel() {
    fun logout() = runBlocking{userRepository.logout()}
}