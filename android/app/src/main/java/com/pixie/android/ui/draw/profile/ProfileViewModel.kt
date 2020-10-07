package com.pixie.android.ui.draw.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.LoggedInUser
import kotlinx.coroutines.runBlocking

class ProfileViewModel(private val userRepository: UserRepository): ViewModel() {
    fun logout() {
        runBlocking {  userRepository.logout()}}
}