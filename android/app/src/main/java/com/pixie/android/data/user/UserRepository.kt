package com.pixie.android.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.R
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.model.user.LoginFormState
import com.pixie.android.model.user.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class UserRepository(val dataSource: UserDataSource) {

    private val loginForm = MutableLiveData<LoginFormState>()
    private val loginResult = MutableLiveData<AuthResult>()
    var user: LoggedInUser? = null

    fun getLoginForm(): LiveData<LoginFormState> {
        return loginForm
    }

    fun setLoginForm(loginFormState: LoginFormState) {
        loginForm.value = loginFormState
    }

    fun getLoginResult(): LiveData<AuthResult> {
        return loginResult
    }

    fun setLoginResult(authResultState: AuthResult) {
        loginResult.postValue(authResultState)
    }

    fun logout() {
        user = null
        loginResult.postValue(null)
        dataSource.logout()
    }

    fun login(username: String, password: String, onLoginResult :(authResult: AuthResult)->Unit) {

        CoroutineScope(IO).launch {
            val response = dataSource.login(username, password)
            lateinit var authResult :AuthResult
            if (response?.login?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.login.user.id.toString(),
                    response.login.user.username,
                    response.login.user.email
                )

                setLoggedInUser(userData)

                authResult=AuthResult(success = LoggedInUserView(displayName = userData.username))
            } else {
                authResult=AuthResult(error = R.string.login_failed)
            }
            // Put job of updating UI back on main thread
            val uiScope = CoroutineScope(Main)
            uiScope.launch {
                onLoginResult(authResult)

            }

        }

    }

    fun register(username: String, email: String, password: String,onLoginResult :(authResult: AuthResult)->Unit) {
        CoroutineScope(IO).launch {
            val response = dataSource.register(username, email, password)
            lateinit var authResult :AuthResult

            if (response?.register?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.register.user.id.toString(),
                    response.register.user.username,
                    response.register.user.email
                )
                setLoggedInUser(userData)
                authResult=AuthResult(success = LoggedInUserView(displayName = userData.username))
            } else {
                authResult=AuthResult(error = R.string.failed_registration)
            }

            // Put job of updating UI back on main thread
            val uiScope = CoroutineScope(Main)
            uiScope.launch {
                onLoginResult(authResult)
            }

        }
    }


    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        private var dataSource:UserDataSource = UserDataSource.getInstance()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: UserRepository(dataSource).also {
                instance = it
            }
        }
    }
}