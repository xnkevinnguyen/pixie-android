package com.pixie.android.ui.draw.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.user.UserRepository

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory(private val chatRepository: ChatRepository):ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(chatRepository) as T
    }
}