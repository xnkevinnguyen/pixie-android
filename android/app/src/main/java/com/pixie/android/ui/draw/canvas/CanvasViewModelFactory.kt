package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.draw.CanvasCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository

@Suppress("UNCHECKED_CAST")
class CanvasViewModelFactory (private val drawingParametersRepository: DrawingParametersRepository,private val canvasCommandHistoryRepository: CanvasCommandHistoryRepository,private val gameSessionRepository: GameSessionRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CanvasViewModel(
            drawingParametersRepository,
            canvasCommandHistoryRepository,
            gameSessionRepository
        ) as T
    }
}