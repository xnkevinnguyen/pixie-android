package com.pixie.android.ui.draw.channelList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.follow.FollowRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.GameSessionData
import java.lang.IndexOutOfBoundsException

class PlayersViewModel(
    private val chatRepository: ChatRepository,
    private val followRepository: FollowRepository,
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

    fun addUserFollowList(user: ChannelParticipant) = followRepository.addUserFollowList(user)

    fun removeUserFollowList(user: ChannelParticipant) = followRepository.removeUserFollowList(user)

    fun isUserInFollowList(user: ChannelParticipant): Boolean =
        followRepository.isUserInFollowList(user)

    fun getGameSession(): LiveData<GameSessionData> {
        return gameSessionRepository.getGameSession()
    }

    fun sendGameInvitation(receiverID: Double, onresult: (RequestResult) -> Unit) {
        gameInviteRepository.sendGameInvitation(receiverID, onresult)
    }
}