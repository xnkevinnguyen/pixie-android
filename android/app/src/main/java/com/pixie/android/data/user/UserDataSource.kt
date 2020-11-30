package com.pixie.android.data.user

import android.util.Log
import com.apollographql.apollo.api.toInput
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.pixie.android.*
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.user.AvatarColorData
import com.pixie.android.type.*

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


    suspend fun register(username:String, password: String, firstName: String, lastName: String, foreground:String,
                         background:String):RegisterMutation.Data?{
        val usernamePasswordInput = UsernamePasswordInput(username,password, firstName, lastName, avatarForeground = foreground.toInput(),
        avatarBackground = background.toInput())
        var response :RegisterMutation.Data? =null
        try{
            response =  apolloClient(null).mutate(RegisterMutation(usernamePasswordInput)).toDeferred().await().data

        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return response
    }
    suspend fun  sendConfig(userID: Double,language: Language,theme:String){
        var themeType:Theme=Theme.DARK
        if(theme =="Christmas"){
            themeType = Theme.CHRISTMAS
        }else if(theme=="Barbie"){
            themeType = Theme.BARBIE
        }else if(theme =="Halloween"){
            themeType = Theme.HALLOWEEN
        }else if(theme =="Light"){
            themeType = Theme.LIGHT
        }
        val input = UserConfigInput(language.toInput(),themeType.toInput())
        try{
            apolloClient(userID).mutate(SetConfigMutation(input)).toDeferred().await()
        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
    }

    suspend fun getAvatarColor(userID: Double): AvatarColorData{
        try{
            val response = apolloClient(userID).query(GetAvatarColorsQuery()).toDeferred().await().data
            val colorQueryData = response?.me
            if (colorQueryData != null) {
                return AvatarColorData(colorQueryData.avatarForeground, colorQueryData.avatarBackground)
            }
        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return AvatarColorData(null, null)
    }

    suspend fun getMe(userID: Double): ChannelParticipant?{
        try{
            val response = apolloClient(userID).query(GetMeQuery()).toDeferred().await().data
            val colorQueryData = response?.me
            if (colorQueryData != null) {
                return ChannelParticipant(colorQueryData.id, colorQueryData.username,
                    colorQueryData.isOnline, colorQueryData.isVirtual,
                    colorQueryData.avatarForeground, colorQueryData.avatarBackground)
            }
        }catch(e:ApolloException){
            Log.d("apolloException", e.message.toString())
        }
        return null
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