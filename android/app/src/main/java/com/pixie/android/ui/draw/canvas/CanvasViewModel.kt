package com.pixie.android.ui.draw.canvas

import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.model.draw.*
import com.pixie.android.type.PathStatus

class CanvasViewModel(
    private val drawingParametersRepository: DrawingParametersRepository,
    private val canvasRepository: CanvasRepository,
    private val gameSessionRepository:GameSessionRepository
) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    fun getDrawCommandHistory() = canvasRepository.getDrawCommandHistory()

    fun sendPoint(x:Float,y:Float, pathStatus: PathStatus, paint:Paint){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        gameSessionRepository.sendManualDrawingPoint(manualPathPointInput)

    }
    fun sendFinalPoint(x:Float,y:Float, pathStatus: PathStatus, paint:Paint,path: ArrayList<PathPoint>){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        //should return an id
        gameSessionRepository.sendManualDrawingFinalPoint(manualPathPointInput){
            val command = CanvasCommand(CommandType.DRAW, paint, path)

            canvasRepository.addCanvasCommand(it,command)
        }

    }

    fun captureEraseAction(x1:Float, y1:Float, x2:Float,y2:Float) {
        //check if coordinates are inside border of previous commands
        val commandHistory = canvasRepository.getDrawCommandHistory().value ?: return
//        var eraserTarget:ArrayList<CanvasCommand> = arrayListOf()
        commandHistory.forEach {
            if(it.value.type == CommandType.DRAW  && !it.value.path.isNullOrEmpty() && !it.value.isErased){
                    var isContact = false
                    it.value.path!!.forEach {

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
                    if(isContact){
                        canvasRepository.eraseCommand(it.key)
                        gameSessionRepository.sendManualCommand(CommandType.ERASE,it.key)
                    }


            }
        }

    }
    fun getIsCanvasLocked()=gameSessionRepository.getIsCanvasLocked()
    

    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

    fun getShouldShowWord() = gameSessionRepository.getShouldShowWord()

}