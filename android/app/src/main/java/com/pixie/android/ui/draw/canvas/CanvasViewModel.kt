package com.pixie.android.ui.draw.canvas

import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.data.draw.DrawingParametersRepository
import com.pixie.android.data.game.GameSessionRepository
import com.pixie.android.data.user.UserRepository
import com.pixie.android.model.draw.*
import com.pixie.android.model.game.GameSessionData
import com.pixie.android.type.PathStatus

class CanvasViewModel(
    private val drawingParametersRepository: DrawingParametersRepository,
    private val canvasRepository: CanvasRepository,
    private val gameSessionRepository:GameSessionRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()
    fun getGameSession(): LiveData<GameSessionData> = gameSessionRepository.getGameSession()
    fun getUserID():Double{
        return userRepository.getUser().userId
    }

    fun getDrawCommandHistory() = canvasRepository.getDrawCommandHistory()

    fun sendPoint(x:Float,y:Float, pathStatus: PathStatus, paint:Paint){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        gameSessionRepository.sendManualDrawingPoint(manualPathPointInput)

    }
    fun sendFinalPoint(x:Float, y:Float, pathStatus: PathStatus, paint:Paint, pathDataPoints: ArrayList<SinglePoint>){
        val manualPathPointInput = ManualPathPointInput(x,y,pathStatus,paint)
        //should return an id
        gameSessionRepository.sendManualDrawingFinalPoint(manualPathPointInput){
            val command = CanvasCommand(CommandType.DRAW, paint, pathDataPoints)

            canvasRepository.addCanvasCommand(it,command)
        }

    }
    fun leaveGame(){
        gameSessionRepository.leaveGame()
    }

    fun captureEraseAction(x1:Float, y1:Float, x2:Float,y2:Float) {
        //check if coordinates are inside border of previous commands
        val commandHistory = canvasRepository.getDrawCommandHistory().value ?: return
//        var eraserTarget:ArrayList<CanvasCommand> = arrayListOf()
        commandHistory.forEach {
            val pathDataPoints = it.value.pathDataPoints

            if(it.value.type == CommandType.DRAW  && !pathDataPoints.isNullOrEmpty() && !it.value.isErased){
                    var isContact = false
                for(n in 0 until pathDataPoints.size -1){

                        //calculate  t = (q − p) × s / (r × s) https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
                        val r = Pair(x2-x1,y2-y1) // P+t*R
                        val s = Pair(pathDataPoints[n+1].x-pathDataPoints[n].x, pathDataPoints[n+1].y-pathDataPoints[n].y) // Q+u*S
                        val productRS = r.first * s.second - r.second * s.first // r x s
                        val divS= Pair(s.first/productRS,s.second/productRS) //  s / (r x s)
                        val subQP = Pair(pathDataPoints[n].x-x1,pathDataPoints[n].y-y1) // q-p
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