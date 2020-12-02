package com.pixie.android.data.user

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.user.*
import com.pixie.android.type.Language
import com.pixie.android.type.Theme
import com.pixie.android.utilities.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class UserRepository(val dataSource: UserDataSource) {

    private val loginForm = MutableLiveData<LoginFormState>()
    private var user: LoggedInUser? = null

    private var avatarColor: AvatarColorData = AvatarColorData(null, null)

    fun getUser(): LoggedInUser {
        val userCopy = user
        if (userCopy != null) {
            return userCopy
        } else {
            throw error("User in UserRepository is null")
        }
    }

    fun getUserSafe():LoggedInUser?{
        return user
    }

    fun getUserAsChannelParticipant(): ChannelParticipant {
        return ChannelParticipant(getUser().userId, getUser().username, true)
    }

    fun getLoginForm(): LiveData<LoginFormState> {
        return loginForm
    }

    fun setLoginForm(loginFormState: LoginFormState) {
        loginForm.value = loginFormState
    }

    fun getAvatarColor(): AvatarColorData{
        fetchAvatarColor()
        return avatarColor
    }

    fun getMe(): ChannelParticipant?{
        return fetchMe()
    }

    private fun fetchAvatarColor(){
        var colors: AvatarColorData
        runBlocking {
            colors = dataSource.getAvatarColor(getUser().userId)
        }
        avatarColor=colors
    }

    private fun fetchMe(): ChannelParticipant?{
        var me:ChannelParticipant?
        runBlocking {
            me = dataSource.getMe(getUser().userId)
        }
        return me
    }

    fun logout() {
        // Logout should ALWAYS be called after exit operations
        // Logout is called when application stops or on manual logout
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                dataSource.logout(getUser().userId)

                CoroutineScope(Dispatchers.Main).launch { user = null }
            }

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
                authResult = AuthResult(
                    LoggedInUserView(
                        userData.username, userData.userId,
                        response.login.user.theme?.rawValue, response.login.user.language
                    )
                )

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
        foreground:String,
        background:String,
        language: Language,
        theme: Theme,
        onLoginResult: (authResult: AuthResult) -> Unit
    ) {
        CoroutineScope(IO).launch {
            val response = dataSource.register(username, password, firstName, lastName, foreground, background, language, theme)
            lateinit var authResult: AuthResult

            if (response?.register?.user?.id != null) {// user needs to exist
                val userData = LoggedInUser(
                    response.register.user.id,
                    response.register.user.username
                )
                setLoggedInUser(userData)
                authResult = AuthResult(
                    success = LoggedInUserView(
                        username = userData.username,
                        userID = userData.userId
                    )
                )
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

    fun sendConfig(language: Language, theme: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.sendConfig(getUser().userId, language, theme)
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