package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.GameData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language
import kotlinx.coroutines.*

typealias GameID = Double

class GameRepository(private val dataSource: GameDataSource,
                     private val userRepository: UserRepository,
                     private val chatRepository: ChatRepository
) {

    //private var availableGames = MutableLiveData<ArrayList<AvailableGameData>>()
    private var availableGames = MutableLiveData<LinkedHashMap<GameID, AvailableGameData>>()

    fun getAvailableGames():LiveData<LinkedHashMap<GameID, AvailableGameData>>{
        return availableGames
    }

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): AvailableGameData? {
        val gameData = createGetGame(mode, difficulty, language)

        if (gameData != null) {
            // suscribe to messages
            chatRepository.addUserChannelMessageSubscription(gameData.gameChannelData)
            //suscribe to participant changes
            chatRepository.addUserChannelParticipantSubscription(gameData.gameChannelData)

            chatRepository.addUserChannels(gameData.gameChannelData)

            availableGames.value?.put(gameData.gameId, gameData)
            Log.d("here", "${gameData}")
            Log.d("here", "repos ${availableGames.value}")
            availableGames.notifyObserver()
        } else {
            Log.d("ApolloException", "Error on createChannel")
        }
        return gameData
    }

    private fun createGetGame(mode: GameMode, difficulty: GameDifficulty, language: Language): AvailableGameData? {
        var gameData: AvailableGameData?
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
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getAvailableGames(mode, difficulty, userRepository.getUser().userId,
                onReceiveMessage = {
                    CoroutineScope(Dispatchers.Main).launch {
                        val gameMap: LinkedHashMap<Double, AvailableGameData> = LinkedHashMap()
                        it?.associateByTo(gameMap, { it.gameId }, { it })
                        availableGames.postValue(gameMap)
                        Log.d("here", "fetch ${availableGames.value}")
                    }
                })

        }

    }

//    fun addAvailableGamePlayerSubscription(mode: GameMode, difficulty: GameDifficulty) {
//        val subscriptionJob = CoroutineScope(Dispatchers.IO).launch {
//            dataSource.subscribeToAvailableGameChange(
//                userRepository.getUser().userId,
//                mode,
//                difficulty,
//                onAvailableGameSessionsChange = {
//                    if (it != null) {
//                        // Main thread only used to modify values
//                        CoroutineScope(Dispatchers.Main).launch {
//                            val gameMap: LinkedHashMap<Double, AvailableGameData> = LinkedHashMap()
//                            it.associateByTo(gameMap, { it.gameId }, { it })
//                            availableGames = MutableLiveData(gameMap)
//                            availableGames.notifyObserver()
//                        }
//                    }
//                })
//        }
//    }


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