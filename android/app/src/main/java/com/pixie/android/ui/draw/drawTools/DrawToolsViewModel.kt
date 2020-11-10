package com.pixie.android.ui.draw.drawTools

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.draw.CommandType

class DrawToolsViewModel(private val drawingParametersRepository: DrawingParametersRepository, private val gameSessionRepository: GameSessionRepository):ViewModel() {

    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()


    fun modifyPrimaryColor(color:Color) = drawingParametersRepository.setPrimaryDrawingColor(color)

    fun modifyStrokeWidth(size: Float) = drawingParametersRepository.setStrokeWidth(size)

    fun modifyCellWidthGrid(width: Int) = drawingParametersRepository.setCellWidthGrid(width)

    fun setEraser(erase: Boolean) = drawingParametersRepository.setErase(erase)

    fun setGridValue(grid: Boolean) = drawingParametersRepository.setGrid(grid)

    fun undo(){
        gameSessionRepository.sendManualCommand(commandType = CommandType.UNDO)
    }
    fun redo(){
        gameSessionRepository.sendManualCommand(commandType = CommandType.REDO)
    }
}