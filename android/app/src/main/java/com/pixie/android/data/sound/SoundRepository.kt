package com.pixie.android.data.sound

import android.content.Context
import android.media.MediaPlayer

class SoundRepository() {

    fun createMediaPlayer(sound: Int, context: Context): MediaPlayer{
         val mediaPlayer = MediaPlayer.create(context, sound)
        return mediaPlayer
    }

    fun startMediaPlayer(mediaPlayer:MediaPlayer) = mediaPlayer.start()

    fun releaseMediaPlayer(mediaPlayer:MediaPlayer) = mediaPlayer.release()

    // Singleton
    companion object {
        @Volatile
        private var instance: SoundRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SoundRepository().also {
                instance = it
            }
        }
    }
}