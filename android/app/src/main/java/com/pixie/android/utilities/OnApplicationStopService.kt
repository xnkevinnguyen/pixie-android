package com.pixie.android.utilities

import android.app.Service
import android.content.Intent

import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.chat.ChatViewModel


class OnApplicationStopService :Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("OnApplicationStopService", "Service Started")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("OnApplicationStopService", "Service Destroyed")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("OnApplicationStopService", "END")

        val intent = Intent(this, LogoutService::class.java)
        startService(intent)

    }
}