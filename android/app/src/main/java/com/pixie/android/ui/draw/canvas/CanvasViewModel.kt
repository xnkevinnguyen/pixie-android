package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository

class CanvasViewModel(private val drawingParametersRepository: DrawingParametersRepository) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()
    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

}