package com.pixie.android.data.user

import android.util.Log
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.type.Language
import com.pixie.android.type.LoginInput
import com.pixie.android.type.UserConfigInput
import com.pixie.android.type.UsernamePasswordInput

class UserDataSource {

    suspend fun login(username: String, password: String):LoginMutation.Data? {
        var response :LoginMutation.Data? =null
         try{
             val loginInput = LoginInput(username,password)
            response =  apolloClient(null).mutate(LoginMutation(loginInput)).toDeferred().await().data

        }catch(e:ApolloException){
             Log.d("apolloException", e.message.toString())
         }
    return response

    }

    suspend fun logout(userID:Double){
        try{

            val result = apolloClient(userID).mutate(LogoutMutation()).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }

    }


    suspend fun register(username:String, password: String, firstName: String, lastName: String):RegisterMutation.Data?{
        val usernamePasswordInput = UsernamePasswordInput(username,password, firstName, lastName)
        var response :RegisterMutation.Data? =null
        try{
            response =  apolloClient(null).mutate(RegisterMutation(usernamePasswordInput)).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return response
    }
    suspend fun  sendConfig(userID: Double,language: Language,theme:String){
        val input = UserConfigInput(language.toInput(),theme.toInput())
        try{
            apolloClient(userID).mutate(SetConfigMutation(input)).toDeferred().await()
        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
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