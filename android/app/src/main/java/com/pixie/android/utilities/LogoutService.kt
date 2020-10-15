package com.pixie.android.utilities

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import kotlinx.coroutines.runBlocking

class LogoutService : IntentService("LogoutService") {
    override fun onHandleIntent(p0: Intent?) {
        Log.d("LogoutService","onHandleIntent")
        val chatRepository = ChatRepository.getInstance()
        val userRepository = UserRepository.getInstance()
        chatRepository.clearChannels()
        runBlocking {
            userRepository.logout()
        }

    }
}