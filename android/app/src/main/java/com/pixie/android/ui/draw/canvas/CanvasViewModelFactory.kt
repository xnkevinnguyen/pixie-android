package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.user.UserRepository

@Suppress("UNCHECKED_CAST")
class CanvasViewModelFactory (private val drawingParametersRepository: DrawingParametersRepository, private val canvasRepository: CanvasRepository, private val gameSessionRepository: GameSessionRepository,private val userRepository: UserRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CanvasViewModel(
            drawingParametersRepository,
            canvasRepository,
            gameSessionRepository,
            userRepository
        ) as T
    }
}