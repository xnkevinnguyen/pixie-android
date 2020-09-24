package com.pixie.android.ui.user.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.user.UserDataSource
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.user.login.LoginViewModel

class RegisterViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {

            return RegisterViewModel(
                userRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}