package com.pixie.android.ui.chat

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelMessageObject
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language

class ChatViewModel(private val chatRepository: ChatRepository, private val soundRepository: SoundRepository,
                    private val gameRepository: GameRepository,private val gameSessionRepository: GameSessionRepository) : ViewModel() {

    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun createMediaPlayer(id: Int, context: Context): MediaPlayer{
        return soundRepository.createMediaPlayer(id, context)
    }

    fun startMediaPlayer(mediaPlayer: MediaPlayer) = soundRepository.startMediaPlayer(mediaPlayer)

    fun releaseMediaPlayer(mediaPlayer: MediaPlayer) = soundRepository.releaseMediaPlayer(mediaPlayer)

    fun setCurrentChannelID(id: Double) {
        chatRepository.setCurrentChannelID(id)
    }

    fun getChannelMessageList() = chatRepository.getChannelMessages()
    fun getCurrentChannelMessageObject(channelID: Double): ChannelMessageObject {
        val messageObject = chatRepository.getChannelMessages().value?.get(channelID)
        return messageObject ?: ChannelMessageObject(arrayListOf())
    }

    fun getChatHistoryCurrentChannel() {
        val channelID = chatRepository.getCurrentChannelID().value
        if (channelID != null) {
            chatRepository.getChatHistory(channelID)
        }
    }

    fun getJoinableChannels() = chatRepository.getJoinableChannels()

    fun joinChannel(channelID: Double) {
        chatRepository.joinChannel(channelID)
    }

    fun createChannel(channelName: String) {
        return chatRepository.createChannel(channelName)
    }

    fun exitGame(gameID: Double){
        gameRepository.exitGame(gameID)
    }

    fun exitChannel(channelID: Double) {
        chatRepository.exitChannel(channelID)
    }

    fun getUserChannels() = chatRepository.getUserChannels()

    fun startChannelsIfNecessary() {
        if (chatRepository.getUserChannels().value.isNullOrEmpty()) {
            // Fetch all channels users has joined
            chatRepository.fetchUserChannels()
            chatRepository.suscribeToUserChannelListChanges()
        }
    }


    fun stopChannel() {
        chatRepository.clearChannels()

    }

    fun sendMessageToCurrentChannel(message: String) {
        val channelID = chatRepository.getCurrentChannelID().value
        if (channelID != null) {
            chatRepository.sendMessage(channelID, message)
        }
    }

    fun startGameSession(gameID: Double, onResult:(RequestResult)->Unit){
        gameSessionRepository.startGame(gameID, onResult)
    }

}