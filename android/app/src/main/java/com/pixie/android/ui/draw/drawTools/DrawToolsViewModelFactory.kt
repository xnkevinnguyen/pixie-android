package com.pixie.android.ui.draw.drawTools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository

@Suppress("UNCHECKED_CAST")
class DrawToolsViewModelFactory(private val drawingParametersRepository: DrawingParametersRepository,private val gameSessionRepository: GameSessionRepository) :ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DrawToolsViewModel(
            drawingParametersRepository,
            gameSessionRepository
        ) as T
    }
}