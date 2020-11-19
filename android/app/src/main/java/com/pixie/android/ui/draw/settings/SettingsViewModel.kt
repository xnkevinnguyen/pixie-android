package com.pixie.android.ui.draw.settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.type.Language

class SettingsViewModel(private val userRepository: UserRepository): ViewModel() {
    fun sendConfig(language: Language, theme:String){
        userRepository.sendConfig(language,theme)
    }
    fun resetUser(loggedInUser: LoggedInUser) = userRepository.setLoggedInUser(loggedInUser)
}