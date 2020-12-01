package com.pixie.android.ui.chat

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.R
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelMessageObject
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameInvitation
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.GameStatus
import com.pixie.android.type.Language

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val soundRepository: SoundRepository,
    private val gameRepository: GameRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val gameInviteRepository: GameInviteRepository
) : ViewModel() {

    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun createMediaPlayer(id: Int, context: Context): MediaPlayer {
        return soundRepository.createMediaPlayer(id, context)
    }

    fun startMediaPlayer(mediaPlayer: MediaPlayer) = soundRepository.startMediaPlayer(mediaPlayer)

    fun releaseMediaPlayer(mediaPlayer: MediaPlayer) =
        soundRepository.releaseMediaPlayer(mediaPlayer)

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

    fun exitGame(gameID: Double) {
        gameRepository.exitGame(gameID)
        gameSessionRepository.leaveGame()
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

    fun startGameSession(gameID: Double, onResult: (RequestResult) -> Unit) {
        gameSessionRepository.startGame(gameID, onResult)
    }

    fun getGameSession(): LiveData<GameSessionData> {
        return gameSessionRepository.getGameSession()
    }

    fun addVirtualPlayer(onResult:(RequestResult)->Unit) {
        val id = getCurrentChannelID().value
        val gameID = getUserChannels().value?.get(id)?.gameID
        if (gameID != null) {
            gameInviteRepository.addVirtualPlayer(gameID,onResult)
        }
    }
    fun sendGameInvitation(receiverID:Double,onresult:(RequestResult)->Unit){
        gameInviteRepository.sendGameInvitation(receiverID,onresult)
    }

    fun subscribeToGameInvitation(){
        gameInviteRepository.subscribeToGameInvitation()
    }
    fun getGameInvitation():LiveData<GameInvitation>{
        return gameInviteRepository.getGameInvitation()
    }
    fun getHasGameInvitationBeenShown():Boolean{
        return gameInviteRepository.getHasInvitationBeenShown()
    }
    fun confirmInvitationBeenShown(){
        gameInviteRepository.confirmInvitationBeenShown()
    }
    fun acceptInvitation(gameID: Double, gameInvite:GameInvitation):Boolean{

        // remove user from old game if they accept invitation
        if(isUserInAGame()){
            val gameInfo = getUserGameData()
            if(gameInfo != null) {
                exitChannel(gameInfo.channelID)
                gameInfo.gameID?.let { exitGame(it) }
            }
        }

        var game:GameSessionData? = null;
        if(gameInvite.status == GameStatus.PENDING) {
            game = gameRepository.joinGame(gameID)
        } else{
            return false
        }

        if(game!=null) {
            gameSessionRepository.subscribeToGameSessionChange(game.id)
            gameSessionRepository.setGameSession(game)
            setCurrentChannelID(game.channelID)
        }

        return true
    }

    fun isUserInAGame():Boolean{
        return chatRepository.isUserInAGame()
    }

    fun getUserGameData():ChannelData?{
        return chatRepository.getUserGameInfo()
    }

}