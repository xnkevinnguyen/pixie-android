package com.pixie.android.ui.draw.channelList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.friend.FriendListRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GameSessionData

class PlayersViewModel(
    private val chatRepository: ChatRepository,
    private val friendListRepository: FriendListRepository,
    private val gameInviteRepository: GameInviteRepository,
    private val gameSessionRepository: GameSessionRepository
) : ViewModel() {
    fun getCurrentChannelID(): LiveData<Double> = chatRepository.getCurrentChannelID()

    fun getCurrentChannelParticipants(channelID: Double?): ArrayList<ChannelParticipant> {

        val participantList = getUserChannels().value?.get(channelID)?.participantList
        if (participantList != null) {
            return ArrayList(participantList)
        }
        return arrayListOf()
    }

    fun getCurrentChannelParticipants(): ArrayList<ChannelParticipant> {
        val currentChannelID = chatRepository.getCurrentChannelID().value
        return getCurrentChannelParticipants(currentChannelID)

    }

    fun getUserChannels() = chatRepository.getUserChannels()

    fun addFriend(userId: Double, onResult: (RequestResult) -> Unit) = friendListRepository.addPlayer(userId, onResult)

    fun removeFriend(userId: Double, onResult: (RequestResult) -> Unit) = friendListRepository.removeFriend(userId, onResult)

//    fun isUserInFollowList(user: ChannelParticipant): Boolean =
//        friendListRepository.isUserInFollowList(user)

    fun fetchFriendList(){
        friendListRepository.fetchFriendList()
    }
    fun getFriendList(): LiveData<ArrayList<ChannelParticipant>>{
        return friendListRepository.getFriendList()
    }

    fun getGameSession(): LiveData<GameSessionData> {
        return gameSessionRepository.getGameSession()
    }

    fun sendGameInvitation(receiverID: Double, onResult: (RequestResult) -> Unit) {
        gameInviteRepository.sendGameInvitation(receiverID, onResult)
    }

    fun removeVirtualPlayer(playerID: Double, onResult: (RequestResult) -> Unit) {
        val id = getCurrentChannelID().value
        val gameID = getUserChannels().value?.get(id)?.gameID
        if (gameID != null) {
            gameInviteRepository.removeVirtualPlayer(gameID,playerID, onResult)
        }

    }
}