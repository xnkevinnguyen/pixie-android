package com.pixie.android.ui.user.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.user.UserDataSource
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.user.login.LoginViewModel

class RegisterViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val loginDataSource = UserDataSource()
            val registerRepository = UserRepository(loginDataSource)

            return RegisterViewModel(
                registerRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}