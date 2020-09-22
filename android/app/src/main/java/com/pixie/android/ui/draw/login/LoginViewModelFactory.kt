package com.pixie.android.ui.draw.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.login.LoginDataSource
import com.pixie.android.data.login.LoginRepository


/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val loginDataSource = LoginDataSource()
            val loginRepository = LoginRepository(loginDataSource)

            // Should be removed when using DI
            loginDataSource.setLoginRepository(loginRepository)
            return LoginViewModel(
                    loginRepository = loginRepository
            ) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}