package com.pixie.android.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.sound.SoundRepository

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory(private val chatRepository: ChatRepository, private val soundRepository: SoundRepository,
                           private val gameRepository: GameRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(chatRepository, soundRepository, gameRepository) as T
    }
}