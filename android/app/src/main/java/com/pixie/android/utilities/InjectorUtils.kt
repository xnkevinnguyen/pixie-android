package com.pixie.android.utilities

import com.pixie.android.data.chat.ChatRepository
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.everybody.EverybodyRepository
import com.pixie.android.data.friend.FriendListRepository
import com.pixie.android.data.game.GameInviteRepository
import com.pixie.android.data.game.GameRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.leaderboard.LeaderboardRepository
import com.pixie.android.data.profile.ProfileRepository
import com.pixie.android.data.sound.SoundRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.ui.draw.canvas.CanvasViewModelFactory
import com.pixie.android.ui.chat.ChatViewModelFactory
import com.pixie.android.ui.chat.GameChatViewModelFactory
import com.pixie.android.ui.draw.availableGames.AvailableGamesViewModelFactory
import com.pixie.android.ui.draw.channelList.ChannelViewModelFactory
import com.pixie.android.ui.draw.channelList.PlayersViewModelFactory
import com.pixie.android.ui.draw.drawTools.DrawToolsViewModelFactory
import com.pixie.android.ui.draw.gameInformation.GameInformationViewModelFactory
import com.pixie.android.ui.user.login.LoginViewModelFactory
import com.pixie.android.ui.draw.home.HomeViewModelFactory
import com.pixie.android.ui.draw.leaderboard.LeaderboardViewModelFactory
import com.pixie.android.ui.draw.profile.ProfileViewModelFactory
import com.pixie.android.ui.draw.settings.SettingsViewModelFactory
import com.pixie.android.ui.user.register.RegisterViewModelFactory

object InjectorUtils {
    fun provideDrawViewModelFactory(): DrawToolsViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        val gameSessionRepository = GameSessionRepository.getInstance()
        return DrawToolsViewModelFactory(
            drawingParametersRepository,gameSessionRepository
        )
    }

    fun provideCanvasViewModelFactory(): CanvasViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        val drawCommandHistoryRepository = CanvasRepository.getInstance()
        val gameSessionRepository = GameSessionRepository.getInstance()
        val userRepository = UserRepository.getInstance()
        return CanvasViewModelFactory(
            drawingParametersRepository,drawCommandHistoryRepository,gameSessionRepository,userRepository
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
        val gameSessionRepository = GameSessionRepository.getInstance()
        val gameInviteRepository = GameInviteRepository.getInstance()
        return ChatViewModelFactory(chatRepository, soundRepository, gameRepository, gameSessionRepository,gameInviteRepository)
    }

    fun provideChannelViewModelFactory(): ChannelViewModelFactory {
        val chatRepository = ChatRepository.getInstance()
        val everybodyRepository = EverybodyRepository.getInstance()
        return ChannelViewModelFactory(chatRepository, everybodyRepository)
    }

    fun providePlayersViewModelFactory(): PlayersViewModelFactory {
        val chatRepository = ChatRepository.getInstance()
        val friendListRepository = FriendListRepository.getInstance()
        val gameInviteRepository = GameInviteRepository.getInstance()
        val gameSessionRepository = GameSessionRepository.getInstance()
        val userRepository = UserRepository.getInstance()
        return PlayersViewModelFactory(chatRepository, friendListRepository,gameInviteRepository,gameSessionRepository,userRepository)
    }

    fun provideAvailableGamesViewModelFactory():AvailableGamesViewModelFactory{
        val gameRepository = GameRepository.getInstance()
        val gameSessionRepository = GameSessionRepository.getInstance()
        return AvailableGamesViewModelFactory(gameRepository,gameSessionRepository)
    }

    fun provideGameInformationViewModelFactory():GameInformationViewModelFactory{
        val gameSessionRepository = GameSessionRepository.getInstance()
        val chatRepository = ChatRepository.getInstance()
        val gameRepository = GameRepository.getInstance()
        val userRepository = UserRepository.getInstance()
        return GameInformationViewModelFactory(gameSessionRepository,chatRepository,gameRepository,userRepository)
    }
    fun provideGameChatViewModelFactory():GameChatViewModelFactory{
        val chatRepository = ChatRepository.getInstance()
        val gameSessionRepository = GameSessionRepository.getInstance()
        return GameChatViewModelFactory(chatRepository,gameSessionRepository)
    }

    fun provideLeaderboardViewModelFactory(): LeaderboardViewModelFactory{
        val leaderboardRepository = LeaderboardRepository.getInstance()
        return LeaderboardViewModelFactory(leaderboardRepository)
    }

}