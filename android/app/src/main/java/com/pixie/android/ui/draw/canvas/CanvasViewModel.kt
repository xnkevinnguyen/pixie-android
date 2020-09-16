package com.pixie.android.ui.draw.canvas

import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.model.draw.DrawCommand

class CanvasViewModel(private val drawingParametersRepository: DrawingParametersRepository) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun getDrawCommandHistory() = drawingParametersRepository.getDrawCommandHistory()

    fun addCommandToHistory(command: DrawCommand) {
        drawingParametersRepository.addDrawCommand(command)
        // Once user adds a command, they lost redo history
        drawingParametersRepository.restoreUndoneCommandList()
    }


}