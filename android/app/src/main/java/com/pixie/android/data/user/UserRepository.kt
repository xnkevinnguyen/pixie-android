package com.pixie.android.data.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.user.LoggedInUser
import com.pixie.android.model.user.LoggedInUserView
import com.pixie.android.model.user.LoginFormState
import com.pixie.android.model.user.AuthResult
import com.pixie.android.utilities.Constants
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
    var user: LoggedInUser? = null

    // Stricly used for operations after logged out
    var loggedOutUserID :Double?=null

    fun getLoginForm(): LiveData<LoginFormState> {
        return loginForm
    }

    fun setLoginForm(loginFormState: LoginFormState) {
        loginForm.value = loginFormState
    }


    suspend fun logout() {
        val userToLogout = user
        // Logout is called when application stops or on manual logout
        if(userToLogout!=null) {
            loggedOutUserID = userToLogout.userId
//            CoroutineScope(IO).launch {
                dataSource.logout(userToLogout.userId)
//            }
            user = null
        }
    }

    fun login(username: String, password: String, onLoginResult: (authResult: AuthResult) -> Unit) {

        CoroutineScope(IO).launch {
            val response = dataSource.login(username, password)
            lateinit var authResult: AuthResult
            if (response?.login?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.login.user.id,
                    response.login.user.username
                )

                setLoggedInUser(userData)
                authResult = AuthResult(success = LoggedInUserView(username = userData.username, userID = userData.userId))

            } else if (!response?.login?.error.isNullOrBlank()) {
                authResult = AuthResult(error = response?.login?.error)

            } else {
                //Abnormal case since server is supposed to give us an error message
                authResult = AuthResult(error = Constants.PLACEHOLDER_AUTH_ERROR)
            }
            // Put job of updating UI back on main thread
            val uiScope = CoroutineScope(Main)
            uiScope.launch {
                onLoginResult(authResult)

            }

        }

    }

    fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        onLoginResult: (authResult: AuthResult) -> Unit
    ) {
        CoroutineScope(IO).launch {
            val response = dataSource.register(username, password, firstName, lastName)
            lateinit var authResult: AuthResult

            if (response?.register?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.register.user.id,
                    response.register.user.username
                )
                setLoggedInUser(userData)
                authResult = AuthResult(success = LoggedInUserView(username = userData.username, userID = userData.userId))
            } else if (!response?.register?.error.isNullOrEmpty()) {
                authResult = AuthResult(error = response?.register?.error)

            } else {
                authResult = AuthResult(error = Constants.PLACEHOLDER_REGISTRATION_ERROR)
            }

            // Put job of updating UI back on main thread
            val uiScope = CoroutineScope(Main)
            uiScope.launch {
                onLoginResult(authResult)
            }

        }
    }


    fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        private var dataSource: UserDataSource = UserDataSource.getInstance()
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: UserRepository(dataSource).also {
                instance = it
            }
        }
    }
}