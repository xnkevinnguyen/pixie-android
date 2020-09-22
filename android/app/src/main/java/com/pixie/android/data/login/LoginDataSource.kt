package com.pixie.android.data.login

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.LoginMutation
import com.pixie.android.apolloClient
import com.pixie.android.model.login.LoggedInUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginDataSource {
    private lateinit var loginRepository: LoginRepository

    fun setLoginRepository(loginRepo:LoginRepository){
        loginRepository=loginRepo
    }

    fun login(username: String, password: String, callback: (loggedInUser: LoggedInUser) -> Unit) {
        GlobalScope.launch {
            apolloClient.mutate(LoginMutation(username, password)).enqueue(
                object : ApolloCall.Callback<LoginMutation.Data>() {

                    override fun onResponse(response: Response<LoginMutation.Data>) {
                        val userData = LoggedInUser(
                            response.data?.login?.user?.id.toString(),
                            response.data?.login?.user?.username.toString()
                        )
                        Result.Success(userData)
                    }

                    override fun onFailure(e: ApolloException) {
                        Log.d("onfailure", "onfailure")
                    }
                }
            )
        }


    }

    fun logout() {
        // TODO: revoke authentication
    }
}