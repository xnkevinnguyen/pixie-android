package com.pixie.android.utilities

import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.ui.draw.canvas.CanvasViewModelFactory
import com.pixie.android.ui.draw.drawTools.DrawToolsViewModelFactory

object InjectorUtils {
    fun provideDrawViewModelFactory(): DrawToolsViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        return DrawToolsViewModelFactory(
            drawingParametersRepository
        )
    }

    fun provideCanvasViewModelFactory(): CanvasViewModelFactory {
        val drawingParametersRepository = DrawingParametersRepository.getInstance()
        return CanvasViewModelFactory(
            drawingParametersRepository
        )
    }
}