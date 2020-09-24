package com.pixie.android.data.user

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.R
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.model.user.LoginFormState
import com.pixie.android.model.user.LoginResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class UserRepository(val dataSource: UserDataSource) {

    private val loginForm = MutableLiveData<LoginFormState>()
    private val loginResult = MutableLiveData<LoginResult>()
    var user: LoggedInUser? = null

    fun getLoginForm(): LiveData<LoginFormState> {
        return loginForm
    }

    fun setLoginForm(loginFormState: LoginFormState) {
        loginForm.value = loginFormState
    }

    fun getLoginResult(): LiveData<LoginResult> {
        return loginResult
    }

    fun setLoginResult(loginResultState: LoginResult) {
        loginResult.postValue(loginResultState)
    }

    fun logout() {
        user = null
        loginResult.postValue(null)
        dataSource.logout()
    }

    fun login(username: String, password: String, f: () -> Unit) {

        CoroutineScope(IO).launch {
            val response = dataSource.login(username, password)
            if (response?.login?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.login.user.id.toString(),
                    response.login.user.username,
                    response.login.user.email
                )
                f
                setLoggedInUser(userData)
                setLoginResult(LoginResult(success = LoggedInUserView(displayName = userData.username)))
            } else {
                setLoginResult(LoginResult(error = R.string.login_failed))
            }

        }

    }

    fun register(username: String, email: String, password: String) {
        CoroutineScope(IO).launch {
            val response = dataSource.register(username, email, password)
            if (response?.register?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.register.user.id.toString(),
                    response.register.user.username,
                    response.register.user.email
                )
                setLoggedInUser(userData)
                setLoginResult(LoginResult(success = LoggedInUserView(displayName = userData.username)))
            } else {
                setLoginResult(LoginResult(error = R.string.failed_registration))
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