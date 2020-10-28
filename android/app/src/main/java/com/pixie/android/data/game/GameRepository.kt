package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.game.CreatedGameData
import com.pixie.android.model.game.GameSessionData
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

    fun removePrevSubscription(){
        for (job in gameSubscriptions){
            job.cancel()
            gameSubscriptions.remove(job)
        }
    }

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): GameSessionData? {
        var gameData :GameSessionData?
        runBlocking {
             gameData =
                dataSource.createGame(mode, difficulty, language, userRepository.getUser().userId)
            {
                // suscribe to channels
                chatRepository.addUserChannelMessageSubscription(it)
                chatRepository.addUserChannelParticipantSubscription(it)
                chatRepository.addUserChannels(it)
            }


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

        removePrevSubscription()
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
                            Log.d("here", "notif 2")
                            Log.d("here", "it $it")
                            availableGames.value = it
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