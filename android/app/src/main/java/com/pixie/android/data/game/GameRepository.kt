package com.pixie.android.data.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.chat.ChannelParticipant
import com.pixie.android.model.game.AvailableGameData
import com.pixie.android.model.game.CreatedGameData
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

    private var availableGames = MutableLiveData<ArrayList<AvailableGameData>>()
    private var userChannels = MutableLiveData<LinkedHashMap<GameID, AvailableGameData>>()
    private var listPlayer = MutableLiveData<ArrayList<ChannelParticipant>>().apply { this.postValue(
        arrayListOf()) }

    fun getListPlayers(gameID: Double):LiveData<ArrayList<ChannelParticipant>>{
        val players = userChannels.value?.get(gameID)?.gameData?.listPlayers
        if(players != null){
            listPlayer.postValue(ArrayList(players))
        }
        return listPlayer
    }

    fun getAvailableGames():LiveData<ArrayList<AvailableGameData>>{
        return availableGames
    }

    fun createGame(mode: GameMode, difficulty: GameDifficulty, language: Language): CreatedGameData? {
        val gameData = createGetGame(mode, difficulty, language)
        if (gameData != null) {
            // suscribe to messages
            chatRepository.addUserChannelMessageSubscription(gameData.gameChannelData)
            //suscribe to participant changes
            chatRepository.addUserChannelParticipantSubscription(gameData.gameChannelData)

            chatRepository.addUserChannels(gameData.gameChannelData)
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
        var availableGames2: ArrayList<AvailableGameData>
        runBlocking {
            availableGames2 = dataSource.getAvailableGames(mode, difficulty, userRepository.getUser().userId)
        }
        availableGames.postValue(availableGames2)
//        val channelMap: LinkedHashMap<Double, AvailableGameData> = LinkedHashMap()
//        availableGames2.associateByTo(channelMap, { it.gameId }, { it })
//        userChannels.postValue(channelMap)
        availableGamesSubscription(mode, difficulty)
    }

    private fun availableGamesSubscription(mode: GameMode, difficulty: GameDifficulty) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSource.subscribeToAvailableGameChange(
                userRepository.getUser().userId,
                mode,
                difficulty,
                onAvailableGameSessionsChange = {
                    if (it != null) {
                        // Main thread only used to modify values
                        CoroutineScope(Dispatchers.Main).launch {
                            availableGames.value = it
                            availableGames.notifyObserver()
                        }
                    }
                })
        }
    }

    fun joinGame(gameID: Double):CreatedGameData? {
        //enter game
        val gameData = enterGame(gameID)
        if (gameData != null) {
            chatRepository.addUserChannelMessageSubscription(gameData.gameChannelData)
            //suscribe to participant changes
            chatRepository.addUserChannelParticipantSubscription(gameData.gameChannelData)

            chatRepository.addUserChannels(gameData.gameChannelData)
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