package com.pixie.android.ui.user.login

import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.user.LoginRepository
import com.pixie.android.model.user.LoginFormState

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    fun getLoginFormState() = loginRepository.getLoginForm()

    fun getLoginResultState() = loginRepository.getLoginResult()

    fun login(username: String, password: String) {
        loginRepository.login(username, password)

    }

    fun loginDataChanged(username: String, password: String) {
        if (!username.isNotBlank()) {
            loginRepository.setLoginForm(LoginFormState(usernameError = R.string.invalid_username))
        }
        else if (!isPasswordValid(password)) {
            loginRepository.setLoginForm(LoginFormState(passwordError = R.string.invalid_password))
        } else {
            loginRepository.setLoginForm(LoginFormState(isDataValid = true))
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 8
    }
}