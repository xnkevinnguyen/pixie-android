package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.model.draw.DrawCommand

class CanvasViewModel(private val drawingParametersRepository: DrawingParametersRepository,private val drawCommandHistoryRepository: DrawCommandHistoryRepository) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun getDrawCommandHistory() = drawCommandHistoryRepository.getDrawCommandHistory()

    fun addCommandToHistory(command: DrawCommand) {
        drawCommandHistoryRepository.addDrawCommand(command)
        // Once user adds a command, they lose redo history
        drawCommandHistoryRepository.restoreUndoneCommandList()
    }

    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

}