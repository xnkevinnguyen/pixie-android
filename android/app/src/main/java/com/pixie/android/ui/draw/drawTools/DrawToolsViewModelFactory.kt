package com.pixie.android.ui.draw.drawTools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.draw.CanvasCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository

@Suppress("UNCHECKED_CAST")
class DrawToolsViewModelFactory(private val drawingParametersRepository: DrawingParametersRepository,private val canvasCommandHistoryRepository: CanvasCommandHistoryRepository) :ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DrawToolsViewModel(
            drawingParametersRepository,
            canvasCommandHistoryRepository
        ) as T
    }
}