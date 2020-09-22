package com.pixie.android.ui.draw.login

import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.login.LoginRepository
import com.pixie.android.data.login.Result
import com.pixie.android.model.login.LoggedInUserView
import com.pixie.android.model.login.LoginFormState
import com.pixie.android.model.login.LoginResult

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    fun getLoginFormState() = loginRepository.getLoginForm()

    fun getLoginResultState() = loginRepository.getLoginResult()

    fun login(username: String, password: String) {
        val result = loginRepository.login(username, password)

        return if (result is Result.Success) {
            loginRepository.setLoginResult(LoginResult(success = LoggedInUserView(displayName = result.data.displayName)))
        } else {
            loginRepository.setLoginResult(LoginResult(error = R.string.login_failed))
        }
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