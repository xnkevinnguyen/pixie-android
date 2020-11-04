package com.pixie.android.ui.draw.canvas

import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.CanvasCommandHistoryRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.draw.*
import com.pixie.android.type.PathStatus

class CanvasViewModel(
    private val drawingParametersRepository: DrawingParametersRepository,
    private val canvasCommandHistoryRepository: CanvasCommandHistoryRepository,
    private val gameSessionRepository:GameSessionRepository
) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun getDrawCommandHistory() = canvasCommandHistoryRepository.getDrawCommandHistory()

    fun sendPoint(x:Float,y:Float, pathStatus: PathStatus, paint:Paint){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        gameSessionRepository.sendManualDrawingFinalPoint(manualPathPointInput)

    }
    fun sendFinalPoint(x:Float,y:Float, pathStatus: PathStatus, paint:Paint,path: ArrayList<PathPoint>){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        //should return an id
        gameSessionRepository.sendManualDrawingFinalPoint(manualPathPointInput){
            //TODO using id returned, make a tagged command
            addCommandToHistory(paint,path)
        }

    }

    fun captureEraseAction(x1:Float, y1:Float, x2:Float,y2:Float) {
        //check if coordinates are inside border of previous commands
        val commandHistory = canvasCommandHistoryRepository.getDrawCommandHistory().value ?: return
        var eraserTarget:ArrayList<CanvasCommand> = arrayListOf()
        commandHistory.forEach {
            if(it.type == CommandType.DRAW  && !it.path.isNullOrEmpty() && !it.isErased){
                    var isContact = false
                    it.path.forEach {
//                        if((it.x2-5) < x1 && x1<(it.x2+5) && (it.y2-5)<y1 && y1<(it.y2+5) ){
//                            isContact = true
//                        }
                        //calculate  t = (q − p) × s / (r × s) https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
                        val r = Pair(x2-x1,y2-y1) // P+t*R
                        val s = Pair(it.x2-it.x1, it.y2-it.y1) // Q+u*S
                        val productRS = r.first * s.second - r.second * s.first // r x s
                        val divS= Pair(s.first/productRS,s.second/productRS) //  s / (r x s)
                        val subQP = Pair(it.x1-x1,it.y1-y1) // q-p
                        val tResult =  subQP.first * divS.second - subQP.second*divS.first
                        val t =tResult.toDouble()

                       // u = (q − p) × r / (r × s)
                        val divR =  Pair(r.first/productRS,r.second/productRS)
                        val uResult = subQP.first * divR.second - subQP.second*divR.first
                        val u=uResult.toDouble()
                        Log.d("u",tResult.toString())

                        if(t.compareTo(0.0)>=0 && t.compareTo(1.0)<=0&&
                            u.compareTo(0.0)>=0 && u.compareTo(1.0)<=0){
                            isContact = true
                        }

                    }

                    if(isContact)eraserTarget.add(it)
//                }
            }
        }
        eraserTarget.forEach{
            val eraseCommand = CanvasCommand(CommandType.ERASE, reference = it)
            eraseCommand.reference?.isErased = true
            canvasCommandHistoryRepository.addCanvasCommand(eraseCommand)
        }
    }

    fun addCommandToHistory(paint: Paint, path: ArrayList<PathPoint>) {

        val drawCommand = CanvasCommand(CommandType.DRAW, paint, path)
        canvasCommandHistoryRepository.addCanvasCommand(drawCommand)
        // Once user adds a command, they lose redo history
        canvasCommandHistoryRepository.restoreUndoneCommandList()
    }

    fun resetDrawCommandHistory() = canvasCommandHistoryRepository.resetDrawCommandHistory()


    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

}