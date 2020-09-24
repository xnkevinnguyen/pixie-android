package com.pixie.android.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.user.UserDataSource
import com.pixie.android.data.user.UserRepository


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory (private val userRepository: UserRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {


            return LoginViewModel(
                  userRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}