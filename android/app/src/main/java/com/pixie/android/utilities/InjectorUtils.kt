package com.pixie.android.utilities

import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.ui.draw.canvas.CanvasViewModelFactory
import com.pixie.android.ui.draw.drawTools.DrawToolsViewModelFactory
import com.pixie.android.ui.draw.home.HomeViewModelFactory
import com.pixie.android.ui.draw.login.LoginViewModelFactory
import com.pixie.android.ui.draw.profile.ProfileViewModelFactory
import com.pixie.android.ui.draw.settings.SettingsViewModelFactory

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

    fun provideHomeViewModelFactory(): HomeViewModelFactory {
        return HomeViewModelFactory()
    }

    fun provideProfileViewModelFactory(): ProfileViewModelFactory {
        return ProfileViewModelFactory()
    }

    fun provideSettingsViewModelFactory(): SettingsViewModelFactory {
        return SettingsViewModelFactory()
    }

    fun provideLoginViewModelFactory(): LoginViewModelFactory {
        return LoginViewModelFactory()
    }
}