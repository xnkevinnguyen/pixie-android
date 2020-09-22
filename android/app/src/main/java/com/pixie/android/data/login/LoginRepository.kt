package com.pixie.android.data.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.LoginMutation
import com.pixie.android.R
import com.pixie.android.apolloClient
import com.pixie.android.model.login.LoggedInUser
import com.pixie.android.model.login.LoggedInUserView
import com.pixie.android.model.login.LoginFormState
import com.pixie.android.model.login.LoginResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    private val loginForm = MutableLiveData<LoginFormState>()
    private val loginResult = MutableLiveData<LoginResult>()
    var user: LoggedInUser? = null
        private set

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
        dataSource.logout()
    }

    fun login(username: String, password: String) {

        //REPLACE WITH COROUTINE & API CALL

        CoroutineScope(IO).launch {
            val response = dataSource.login(username, password)
            if (response?.login?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response?.login?.user?.id.toString(),
                    response?.login?.user?.username.toString()
                )
                setLoggedInUser(userData)
                setLoginResult(LoginResult(success = LoggedInUserView(displayName = userData.displayName)))
            } else {
                setLoginResult(LoginResult(error = R.string.login_failed))
            }

        }


    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
    }
}