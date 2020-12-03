package com.pixie.android.ui.user.register

import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.user.AuthResult
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.model.user.LoginFormState
import com.pixie.android.type.Language
import com.pixie.android.type.Theme

class RegisterViewModel (private val userRepository: UserRepository) : ViewModel() {

    fun getRegisterFormState() = userRepository.getLoginForm()

    fun userPreviousLogin(userID:Double, username: String){
        userRepository.setLoggedInUser(LoggedInUser(userID,username))
    }

    fun register(username: String, password: String, firstName: String, lastName: String, foreground:String, background:String,
                 language:Language, theme: Theme,
                 onRegisterResult :(authResult: AuthResult)->Unit) {

        userRepository.register(username, password, firstName, lastName, foreground, background, language, theme, onRegisterResult)

    }
    fun login(username: String, password: String,onLoginResult:(authResult:AuthResult)->Unit){
        userRepository.login(username,password,onLoginResult )
    }


    fun registerDataChanged(name:String, surname: String, username: String, password: String, reTypePassword:String) {
        if (!name.isNotBlank() || !surname.isNotBlank()){
            userRepository.setLoginForm(LoginFormState(usernameError = R.string.invalid_name))
        }
        else if (!username.isNotBlank()) {
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
        return password.length >= 6
    }
}