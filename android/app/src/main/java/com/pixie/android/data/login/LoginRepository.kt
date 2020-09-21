package com.pixie.android.data.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.login.LoggedInUser
import com.pixie.android.model.login.LoginFormState
import com.pixie.android.model.login.LoginResult

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    private val loginForm = MutableLiveData<LoginFormState>()
    private val loginResult = MutableLiveData<LoginResult>()
    var user: LoggedInUser? = null
        private set

    fun getLoginForm() : LiveData<LoginFormState>{
        return loginForm
    }

    fun setLoginForm(loginFormState: LoginFormState) {
        loginForm.value = loginFormState
    }

    fun getLoginResult() : LiveData<LoginResult>{
        return loginResult
    }

    fun setLoginResult(loginResultState: LoginResult){
        loginResult.value = loginResultState
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}