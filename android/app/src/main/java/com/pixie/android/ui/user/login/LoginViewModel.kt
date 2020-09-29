package com.pixie.android.ui.user.login

import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.LoginFormState
import com.pixie.android.model.user.AuthResult
import com.pixie.android.model.user.LoggedInUser

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getLoginFormState() = userRepository.getLoginForm()


    fun userPreviousLogin(userID:Double, username: String){
        userRepository.setLoggedInUser(LoggedInUser(userID,username))
    }
    fun login(username: String, password: String, onLoginResult :(authResult: AuthResult)->Unit) {
        userRepository.login(username, password, onLoginResult)
    }

    fun loginDataChanged(username: String, password: String) {
        if (!username.isNotBlank()) {
            userRepository.setLoginForm(LoginFormState(usernameError = R.string.invalid_username))
        }
        else if (!isPasswordValid(password)) {
            userRepository.setLoginForm(LoginFormState(passwordError = R.string.invalid_password))
        } else {
            userRepository.setLoginForm(LoginFormState(isDataValid = true))
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }
}