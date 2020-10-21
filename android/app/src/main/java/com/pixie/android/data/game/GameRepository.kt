package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.game.AvailableGame
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

    private var availableGames = MutableLiveData<LinkedHashMap<GameID, AvailableGame>>()

    private var gamePlayerSubscriptions = HashMap<GameID, Job>()

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): GameData? {
        val gameData = createGetGame(mode, difficulty, language)

        if (gameData != null) {
            // suscribe to messages
            chatRepository.addUserChannelMessageSubscription(gameData.channelData)
            //suscribe to participant changes
            chatRepository.addUserChannelParticipantSubscription(gameData.channelData)

            chatRepository.addUserChannels(gameData.channelData)
        } else {
            Log.d("ApolloException", "Error on createChannel")
        }
        return gameData
    }

    private fun createGetGame(mode: GameMode, difficulty: GameDifficulty, language: Language): GameData? {
        var gameData: GameData?
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

    fun fetchAvailableGames(mode: GameMode, difficulty: GameDifficulty, language: Language) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.getAvailableGames(mode, difficulty, language, userRepository.getUser().userId,
                onReceiveMessage = {
                if (it != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val availableGameMap: LinkedHashMap<Double, AvailableGame> = LinkedHashMap()
                        it.associateByTo(availableGameMap, { it.gameId }, { it.availableGame })
                        availableGames.postValue(availableGameMap)
                        //suscribe to participant changes
                        //suscribeToUserChannelsParticipants(it)
                    }
                }
            })

        }

    }

    fun addAvailableGamePlayerSubscription(mode: GameMode, difficulty: GameDifficulty) {
        val subscriptionJob = CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToAvailableGameChange(
                userRepository.getUser().userId,
                mode,
                difficulty,
                onChannelChange = {
                    if (it != null) {
                        // Main thread only used to modify values
                        CoroutineScope(Dispatchers.Main).launch {
                            availableGames.value?.get(it)?.participantList =
                                it.participantList
                            userChannels.notifyObserver()

                            val availableGameMap: LinkedHashMap<Double, Job> = LinkedHashMap()
                            it.associateByTo(availableGameMap, { it.gameId }, { subscriptionJob })
                        }
                    }
                })
        }
        gamePlayerSubscriptions.put(channelData.channelID, subscriptionJob)
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
}