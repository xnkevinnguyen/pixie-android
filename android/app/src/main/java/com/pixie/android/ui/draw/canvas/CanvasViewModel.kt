package com.pixie.android.ui.draw.canvas

import android.graphics.Paint
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.CanvasCommandHistoryRepostiroy
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.model.draw.Border
import com.pixie.android.model.draw.CommandType
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.PathPoint

class CanvasViewModel(
    private val drawingParametersRepository: DrawingParametersRepository,
    private val canvasCommandHistoryRepostiroy: CanvasCommandHistoryRepostiroy
) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun getDrawCommandHistory() = canvasCommandHistoryRepostiroy.getDrawCommandHistory()


    fun captureEraseAction(x:Float,y:Float) {
        //check if coordinates are inside border of previous commands
        val commandHistory = canvasCommandHistoryRepostiroy.getDrawCommandHistory().value ?: return
        var eraserTarget:ArrayList<CanvasCommand> = arrayListOf()
        commandHistory.forEach {
            if(it.type == CommandType.DRAW && it.border !=null && !it.path.isNullOrEmpty() && !it.isErased){
                if (it.border.xMin <= x && x <=it.border.xMax && it.border.yMin<=y && y<=it.border.yMax){
                    var isContact = false
                    it.path.forEach {
                        if((it.x2-5) < x && x<(it.x2+5) && (it.y2-5)<y && y<(it.y2+5) ){
                            isContact = true
                        }
                    }

                    if(isContact)eraserTarget.add(it)
                }
            }
        }
        eraserTarget.forEach{
            val eraseCommand = CanvasCommand(CommandType.ERASE, reference = it)
            eraseCommand.reference?.isErased = true
            canvasCommandHistoryRepostiroy.addCanvasCommand(eraseCommand)
        }
    }

    fun addCommandToHistory(paint: Paint, path: ArrayList<PathPoint>) {
        //calculate border
        var xMin = path.first().x1;var yMin = path.first().y1
        var xMax = path.first().x1 ;var yMax = path.first().y1
        path.forEach {
            if(it.x2<xMin){
                xMin = it.x2
            }else if(it.x2>xMax){
                xMax=it.x2
            }
            if(it.y2<yMin){
                yMin=it.y2
            }else if(it.y2>yMax){
                yMax=it.y2
            }

        }
        val drawCommand = CanvasCommand(CommandType.DRAW, paint, path, Border(xMin,yMin,xMax,yMax))
        canvasCommandHistoryRepostiroy.addCanvasCommand(drawCommand)
        // Once user adds a command, they lose redo history
        canvasCommandHistoryRepostiroy.restoreUndoneCommandList()
    }

    fun resetDrawCommandHistory() = canvasCommandHistoryRepostiroy.resetDrawCommandHistory()


    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

}