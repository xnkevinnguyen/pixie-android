package com.pixie.android.data.game

import android.util.Log
import com.pixie.android.data.chat.ChatDataSource
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.chat.ChannelData
import com.pixie.android.model.game.GameData
import com.pixie.android.type.GameDifficulty
import com.pixie.android.type.GameMode
import com.pixie.android.type.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameRepository(private val dataSource: GameDataSource,
                     private val userRepository: UserRepository,
                     private val chatRepository: ChatRepository
) {

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