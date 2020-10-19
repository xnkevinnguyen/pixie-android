package com.pixie.android.ui.draw.drawTools

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.CanvasCommandHistoryRepostiroy
import com.pixie.android.data.draw.DrawingParametersRepository

class DrawToolsViewModel(private val drawingParametersRepository: DrawingParametersRepository, private val canvasCommandHistoryRepostiroy: CanvasCommandHistoryRepostiroy):ViewModel() {

    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()


    fun modifyPrimaryColor(color:Color) = drawingParametersRepository.setPrimaryDrawingColor(color)

    fun modifyStrokeWidth(size: Float) = drawingParametersRepository.setStrokeWidth(size)

    fun modifyCellWidthGrid(width: Int) = drawingParametersRepository.setCellWidthGrid(width)

    fun setEraser(erase: Boolean) = drawingParametersRepository.setErase(erase)

    fun setGridValue(grid: Boolean) = drawingParametersRepository.setGrid(grid)

    fun undo(){
        canvasCommandHistoryRepostiroy.popLastDrawCommandFromHistory()
    }
    fun redo(){
        canvasCommandHistoryRepostiroy.popUndoneCommand()
    }
}