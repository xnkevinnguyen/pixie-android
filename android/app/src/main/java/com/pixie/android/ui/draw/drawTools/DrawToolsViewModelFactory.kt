package com.pixie.android.ui.draw.drawTools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository

@Suppress("UNCHECKED_CAST")
class DrawToolsViewModelFactory(private val drawingParametersRepository: DrawingParametersRepository,private val drawCommandHistoryRepository: DrawCommandHistoryRepository) :ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DrawToolsViewModel(
            drawingParametersRepository,
            drawCommandHistoryRepository
        ) as T
    }
}