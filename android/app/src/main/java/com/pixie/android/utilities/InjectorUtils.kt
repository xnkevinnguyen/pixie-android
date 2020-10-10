package com.pixie.android.utilities

import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.canvas.CanvasViewModelFactory
import com.pixie.android.ui.chat.ChatViewModelFactory
import com.pixie.android.ui.draw.channelList.ChannelViewModelFactory
import com.pixie.android.ui.draw.channelList.PlayersViewModelFactory
import com.pixie.android.ui.draw.drawTools.DrawToolsViewModelFactory
import com.pixie.android.ui.user.login.LoginViewModelFactory
import com.pixie.android.ui.draw.home.HomeViewModelFactory
import com.pixie.android.ui.draw.profile.ProfileViewModelFactory
import com.pixie.android.ui.draw.settings.SettingsViewModelFactory
import com.pixie.android.ui.user.register.RegisterViewModelFactory

object InjectorUtils {
    fun provideDrawViewModelFactory(): DrawToolsViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        val drawCommandHistoryRepository = DrawCommandHistoryRepository.getInstance()
        return DrawToolsViewModelFactory(
            drawingParametersRepository,drawCommandHistoryRepository
        )
    }

    fun provideCanvasViewModelFactory(): CanvasViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        val drawCommandHistoryRepository = DrawCommandHistoryRepository.getInstance()
        return CanvasViewModelFactory(
            drawingParametersRepository,drawCommandHistoryRepository
        )
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        val userRepository = UserRepository.getInstance()

        return LoginViewModelFactory(userRepository)
    }

    fun provideRegisterViewModelFactory(): RegisterViewModelFactory {
        val userRepository = UserRepository.getInstance()

        return RegisterViewModelFactory(userRepository)
    }

    fun provideHomeViewModelFactory(): HomeViewModelFactory {
        val userRepository = UserRepository.getInstance()
        return HomeViewModelFactory(userRepository)
    }

    fun provideProfileViewModelFactory(): ProfileViewModelFactory {
        val userRepository = UserRepository.getInstance()
        return ProfileViewModelFactory(userRepository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val userRepository = UserRepository.getInstance()
        return SettingsViewModelFactory(userRepository)

    }
    fun provideChatViewModelFactory():ChatViewModelFactory{
        val chatRepository = ChatRepository.getInstance()
        return ChatViewModelFactory(chatRepository)
    }

    fun provideChannelViewModelFactory(): ChannelViewModelFactory {
        return ChannelViewModelFactory()
    }

    fun providePlayersViewModelFactory(): PlayersViewModelFactory {
        val chatRepository = ChatRepository.getInstance()
        return PlayersViewModelFactory(chatRepository)
    }
}