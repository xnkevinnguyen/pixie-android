package com.pixie.android.data.login

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.toDeferred
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

    suspend fun login(username: String, password: String):LoginMutation.Data? {
        var response :LoginMutation.Data? =null
         try{
            response =  apolloClient.mutate(LoginMutation(username, password)).toDeferred().await().data

        }catch(e:ApolloException){
             Log.d("apolloException", e.message.toString())
         }
    return response

    }

    fun logout() {
        // TODO: revoke authentication
    }
}