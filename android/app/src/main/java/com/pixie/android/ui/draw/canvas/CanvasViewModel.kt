package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository

class CanvasViewModel(private val drawingParametersRepository: DrawingParametersRepository) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()
}