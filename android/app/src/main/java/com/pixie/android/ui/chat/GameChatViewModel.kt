package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.chat.ChannelMessageObject
enum class MESSAGE_TYPE{
    GUESS,
    MESSAGE

}
class GameChatViewModel(private val chatRepository: ChatRepository, private val gameSessionRepository: GameSessionRepository): ViewModel() {

    private var messageType:MESSAGE_TYPE = MESSAGE_TYPE.MESSAGE
    fun getMessageType():MESSAGE_TYPE{
        return messageType
    }
    fun setMessageType(type:MESSAGE_TYPE){
        messageType = type
    }
    fun getGameChannelID() = gameSessionRepository.getGameChannelID()
    // non optimal since this will refresh a change on any channels
    fun getChannelMessageList() = chatRepository.getChannelMessages()
    fun sendMessageOrGuess(message: String, onResult:(Boolean?)->Unit){
        if(messageType.equals(MESSAGE_TYPE.MESSAGE)){
            sendMessageToGameChannel(message)
            // OnResult null when it is a message and not a guess
            onResult(null)
        }else if(messageType.equals(MESSAGE_TYPE.GUESS)){
            gameSessionRepository.guessWord(message,onResult)
        }
    }

    fun sendMessageToGameChannel(message: String) {
        val channelID = getGameChannelID()
        chatRepository.sendMessage(channelID, message)

    }
}