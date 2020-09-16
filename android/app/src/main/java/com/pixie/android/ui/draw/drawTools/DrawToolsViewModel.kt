package com.pixie.android.ui.draw.drawTools

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository
import kotlin.random.Random

class DrawToolsViewModel(private val drawingParametersRepository: DrawingParametersRepository):ViewModel() {

    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun modifyPrimaryColor(color:Color) = drawingParametersRepository.setPrimaryDrawingColor(color)

    fun modifyStrokeWidth(size: Float) = drawingParametersRepository.setStrokeWidth(size)

    fun modifyCellWidthGrid(width: Int) = drawingParametersRepository.setCellWidthGrid(width)

    fun setEraser(erase: Boolean) = drawingParametersRepository.setErase(erase)

    fun setGridValue(grid: Boolean) = drawingParametersRepository.setGrid(grid)
}