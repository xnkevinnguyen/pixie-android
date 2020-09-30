package com.pixie.android.ui.user.register

import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.AuthResult
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.model.user.LoginFormState

class RegisterViewModel (private val userRepository: UserRepository) : ViewModel() {

    fun getRegisterFormState() = userRepository.getLoginForm()

    fun userPreviousLogin(userID:Double, username: String){
        userRepository.setLoggedInUser(LoggedInUser(userID,username))
    }

    fun register(username: String, password: String, onRegisterResult :(authResult: AuthResult)->Unit) {

        userRepository.register(username, password,onRegisterResult)

    }

    fun registerDataChanged(username: String, password: String, reTypePassword:String) {
        if (!username.isNotBlank()) {
            userRepository.setLoginForm(LoginFormState(usernameError = R.string.invalid_username))
        }
        else if (!isPasswordValid(password)) {
            userRepository.setLoginForm(LoginFormState(passwordError = R.string.invalid_password))
        }
        else if (reTypePassword != password ) {
            userRepository.setLoginForm(LoginFormState(rePasswordError = R.string.invalid_re_password))
        } else {
            userRepository.setLoginForm(LoginFormState(isDataValid = true))
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5
    }
}