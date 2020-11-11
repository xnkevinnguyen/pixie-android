package com.pixie.android.data.game

import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.game.GameInvitation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameInviteRepository(
    private val dataSource: GameInviteDataSource,
    private val userRepository: UserRepository,
    private val gameSessionRepository: GameSessionRepository
) {
    private var gameInvitation = MutableLiveData<GameInvitation>()
    private var hasInvitationBeenShown: Boolean = false
    private var subscription: Job? = null
    fun getGameInvitation() = gameInvitation
    fun getHasInvitationBeenShown() = hasInvitationBeenShown
    fun confirmInvitationBeenShown() {
        hasInvitationBeenShown = true
    }

    fun addVirtualPlayer(gameID: Double, onResult: (RequestResult) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = dataSource.addVirtualPlayer(
                gameID,
                userRepository.getUser().userId

            )
            CoroutineScope(Dispatchers.Main).launch {
                onResult(RequestResult(result))
            }

        }
    }

    fun sendGameInvitation(receiverID: Double, onResult: (RequestResult) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            dataSource.sendGameInvitation(
                gameSessionRepository.getGameSessionID(),
                userRepository.getUser().userId,
                receiverID
            ) {
                CoroutineScope(Dispatchers.Main).launch {
                    onResult(it)
                }
            }
        }
    }

    fun subscribeToGameInvitation() {
        subscription?.cancel()
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToGameInvitation(userRepository.getUser().userId) {
                hasInvitationBeenShown = false
                gameInvitation.postValue(it)
            }
        }
        subscription = job
    }

    // Singleton
    companion object {
        @Volatile
        private var instance: GameInviteRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val dataSource = GameInviteDataSource()
            instance ?: GameInviteRepository(
                dataSource,
                UserRepository.getInstance(),
                GameSessionRepository.getInstance()
            ).also {
                instance = it
            }
        }
    }

    // Function to make sure observer is notified when a data structure is modified
    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}