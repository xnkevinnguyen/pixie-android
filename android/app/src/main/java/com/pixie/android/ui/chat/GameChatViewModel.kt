package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.chat.ChannelMessageObject

class GameChatViewModel(private val chatRepository: ChatRepository, private val gameSessionRepository: GameSessionRepository): ViewModel() {

    fun getGameChannelID() = gameSessionRepository.getGameChannelID()
    // non optimal since this will refresh a change on any channels
    fun getChannelMessageList() = chatRepository.getChannelMessages()


    fun sendMessageToGameChannel(message: String) {
        val channelID = getGameChannelID()
        chatRepository.sendMessage(channelID, message)

    }
}