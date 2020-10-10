package com.pixie.android.ui.draw.settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.LoggedInUser

class SettingsViewModel(private val userRepository: UserRepository): ViewModel() {
    fun resetUser(loggedInUser: LoggedInUser) = userRepository.setLoggedInUser(loggedInUser)
    fun getUser() = userRepository.user

}