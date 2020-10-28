package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.RequestResult
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.model.game.GameData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language
import kotlinx.coroutines.*

class GameRepository(private val dataSource: GameDataSource,
                     private val userRepository: UserRepository,
                     private val chatRepository: ChatRepository
) {

    private var availableGames = MutableLiveData<ArrayList<CreatedGameData>>()
    private var gameSubscriptions= arrayListOf<Job>()

    fun getAvailableGames():LiveData<ArrayList<CreatedGameData>>{
        return availableGames
    }

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): CreatedGameData? {
        val gameData = createGetGame(mode, difficulty, language)
        if (gameData != null) {
            // suscribe to messages
            gameData.gameChannelData?.let { chatRepository.addUserChannelMessageSubscription(it) }
            //suscribe to participant changes
            gameData.gameChannelData?.let { chatRepository.addUserChannelParticipantSubscription(it) }

            gameData.gameChannelData?.let { chatRepository.addUserChannels(it) }
            Log.d("here", "notif 1")
            availableGames.notifyObserver()
        } else {
            Log.d("ApolloException", "Error on createChannel")
        }
        return gameData
    }

    private fun createGetGame(mode: GameMode, difficulty: GameDifficulty, language: Language): CreatedGameData? {
        var gameData: CreatedGameData?
        runBlocking {
            gameData =
                dataSource.createGame(mode, difficulty, language, userRepository.getUser().userId)
        }
        return gameData
    }


    fun exitGame(gameID: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val userID = userRepository.getUser().userId
            dataSource.exitGame(gameID, userID)
        }
    }

    fun fetchAvailableGames(mode: GameMode, difficulty: GameDifficulty) {
        var availableGames2: ArrayList<CreatedGameData>
        runBlocking {
            availableGames2 = dataSource.getAvailableGames(mode, difficulty, userRepository.getUser().userId)
        }
        availableGames.postValue(availableGames2)
        gameSubscriptions.clear()

        availableGamesSubscription(mode, difficulty)
    }

    private fun availableGamesSubscription(mode: GameMode, difficulty: GameDifficulty) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToAvailableGameChange(
                userRepository.getUser().userId,
                mode,
                difficulty,
                onAvailableGameSessionsChange = {
                    if (it != null) {
                        // Main thread only used to modify values
                        CoroutineScope(Dispatchers.Main).launch {
                            availableGames.value = it
                            Log.d("here", "notif 2")
                            availableGames.notifyObserver()
                        }
                    }
                })
        }
        gameSubscriptions.add(job)
    }

    fun joinGame(gameID: Double):CreatedGameData? {
        //enter game
        val gameData = enterGame(gameID)
        if (gameData != null) {
            gameData.gameChannelData?.let { chatRepository.addUserChannelMessageSubscription(it) }
            //suscribe to participant changes
            gameData.gameChannelData?.let { chatRepository.addUserChannelParticipantSubscription(it) }

            gameData.gameChannelData?.let { chatRepository.addUserChannels(it) }

        } else {
            Log.d("ApolloException", "Error on joinChannel")
        }
        return gameData
    }

    fun enterGame(gameID: Double): CreatedGameData? {
        var gameData: CreatedGameData?
        runBlocking {
            gameData =
                dataSource.enterGame(gameID, userRepository.getUser().userId)
        }
        return gameData
    }



    // Singleton
    companion object {
        @Volatile
        private var instance: GameRepository? = null
        fun getInstance() = instance ?: synchronized(this) {

            val gameDataSource = GameDataSource()
            instance ?: GameRepository(gameDataSource, UserRepository.getInstance(), ChatRepository.getInstance()).also {
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