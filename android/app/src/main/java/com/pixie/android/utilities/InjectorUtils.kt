package com.pixie.android.utilities

import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.draw.CanvasCommandHistoryRepostiroy
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.canvas.CanvasViewModelFactory
import com.pixie.android.ui.chat.ChatViewModelFactory
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModelFactory
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
        val drawCommandHistoryRepository = CanvasCommandHistoryRepostiroy.getInstance()
        return DrawToolsViewModelFactory(
            drawingParametersRepository,drawCommandHistoryRepository
        )
    }

    fun provideCanvasViewModelFactory(): CanvasViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        val drawCommandHistoryRepository = CanvasCommandHistoryRepostiroy.getInstance()
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
        val profileRepository = ProfileRepository.getInstance()
        return ProfileViewModelFactory(userRepository, profileRepository)
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        val userRepository = UserRepository.getInstance()
        return SettingsViewModelFactory(userRepository)

    }
    fun provideChatViewModelFactory():ChatViewModelFactory{
        val chatRepository = ChatRepository.getInstance()
        val soundRepository = SoundRepository.getInstance()
        val gameRepository = GameRepository.getInstance()
        return ChatViewModelFactory(chatRepository, soundRepository, gameRepository)
    }

    fun provideChannelViewModelFactory(): ChannelViewModelFactory {
        val chatRepository = ChatRepository.getInstance()
        return ChannelViewModelFactory(chatRepository)
    }

    fun providePlayersViewModelFactory(): PlayersViewModelFactory {
        val chatRepository = ChatRepository.getInstance()
        return PlayersViewModelFactory(chatRepository)
    }

    fun provideAvailableGamesViewModelFactory():AvailableGamesViewModelFactory{
        val gameRepository = GameRepository.getInstance()
        return AvailableGamesViewModelFactory(gameRepository)
    }

}