package com.pixie.android.data.user

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.LoginMutation
import com.pixie.android.RegisterMutation
import com.pixie.android.apolloClient
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.type.UsernamePasswordInput

class UserDataSource {

    suspend fun login(username: String, password: String):LoginMutation.Data? {
        var response :LoginMutation.Data? =null
         try{
            response =  apolloClient.mutate(LoginMutation(username, password)).toDeferred().await().data

        }catch(e:ApolloException){
             Log.d("apolloException", e.message.toString())
         }
    return response

    }

    suspend fun register(username:String, email:String, password: String ):RegisterMutation.Data?{
        val usernamePasswordInput = UsernamePasswordInput(email,username,password)
        var response :RegisterMutation.Data? =null
        try{
            response =  apolloClient.mutate(RegisterMutation(usernamePasswordInput)).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return response
    }

    fun logout() {
        // TODO: revoke authentication
    }
    // Singleton
    companion object {
        @Volatile
        private var instance: UserDataSource? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: UserDataSource().also {
                instance = it
            }
        }
    }
}