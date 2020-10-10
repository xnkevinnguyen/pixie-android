package com.pixie.android.data.user

import android.util.Log
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.LoginMutation
import com.pixie.android.LogoutMutation
import com.pixie.android.RegisterMutation
import com.pixie.android.apolloClient
import com.pixie.android.type.LoginInput
import com.pixie.android.type.LogoutInput
import com.pixie.android.type.UsernamePasswordInput

class UserDataSource {

    suspend fun login(username: String, password: String):LoginMutation.Data? {
        var response :LoginMutation.Data? =null
         try{
             val loginInput = LoginInput(username,password)
            response =  apolloClient.mutate(LoginMutation(loginInput)).toDeferred().await().data

        }catch(e:ApolloException){
             Log.d("apolloException", e.message.toString())
         }
    return response

    }

    suspend fun logout(userID:Double){
        val input : LogoutInput = LogoutInput(userID)
        try{

            val result = apolloClient.mutate(LogoutMutation(input)).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }

    }


    suspend fun register(username:String, password: String, firstName: String, lastName: String):RegisterMutation.Data?{
        val usernamePasswordInput = UsernamePasswordInput(username,password, firstName, lastName)
        var response :RegisterMutation.Data? =null
        try{
            response =  apolloClient.mutate(RegisterMutation(usernamePasswordInput)).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return response
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