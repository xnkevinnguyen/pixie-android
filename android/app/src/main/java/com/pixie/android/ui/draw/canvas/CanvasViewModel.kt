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
    private val gameSessionRepository: GameSessionRepository,
    private val userRepository: UserRepository
) :
    ViewModel() {
    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()
    fun getGameSession(): LiveData<GameSessionData> = gameSessionRepository.getGameSession()
    fun getUserID(): Double {
        return userRepository.getUser().userId
    }

    fun getDrawCommandHistory() = canvasRepository.getDrawCommandHistory()

    fun sendPoint(x: Float, y: Float, pathStatus: PathStatus, paint: Paint) {
        val manualPathPointInput = ManualPathPointInput(x, y, pathStatus, paint)
        gameSessionRepository.sendManualDrawingPoint(manualPathPointInput)

    }

    fun sendFinalPoint(
        x: Float,
        y: Float,
        pathStatus: PathStatus,
        paint: Paint,
        pathDataPoints: ArrayList<SinglePoint>
    ) {
        val manualPathPointInput = ManualPathPointInput(x, y, pathStatus, paint)
        //should return an id
        val ID=gameSessionRepository.sendManualDrawingFinalPoint(manualPathPointInput)
        val command = CanvasCommand(CommandType.DRAW, paint, pathDataPoints)

        canvasRepository.addCanvasCommand(ID, command)

    }

    fun leaveGame() {
        gameSessionRepository.leaveGame()
    }

    fun captureEraseActionTouch(x: Float, y: Float) {
        val eraserWidth = drawingParametersRepository.getEraseWidth()
        val xy1List = arrayListOf<Pair<Float, Float>>()
        xy1List.add(Pair(x - eraserWidth, y - eraserWidth))
        xy1List.add(Pair(x + eraserWidth, y - eraserWidth))
        xy1List.add(Pair(x - eraserWidth, y + eraserWidth))
        xy1List.add(Pair(x + eraserWidth, y + eraserWidth))
        xy1List.add(Pair(x, y))

        val commandHistory = canvasRepository.getDrawCommandHistory().value ?: return

        commandHistory.forEach {
            val pathDataPoints = it.value.pathDataPoints

            if (it.value.type == CommandType.DRAW && !pathDataPoints.isNullOrEmpty() && !it.value.isErased) {
                var isContact = false
                for (n in 0 until pathDataPoints.size - 1) {
                    //check if there is a point inside
                    if (xy1List.get(0).first <= pathDataPoints[n].x && pathDataPoints[n].x <= xy1List.get(
                            3
                        ).first
                        && xy1List.get(0).second <= pathDataPoints[n].y && pathDataPoints[n].y <= xy1List.get(
                            3
                        ).second
                    )
                        isContact = true


                }
                if (isContact) {
                    canvasRepository.eraseCommand(it.key)
                    gameSessionRepository.sendManualCommand(CommandType.ERASE, it.key)
                }


            }
        }

    }

    fun captureEraseAction(x1: Float, y1: Float, x2: Float, y2: Float) {
        val eraserWidth = drawingParametersRepository.getEraseWidth()
        val xy1List = arrayListOf<Pair<Float, Float>>()
        xy1List.add(Pair(x1 - eraserWidth, y1 - eraserWidth))
        xy1List.add(Pair(x1 + eraserWidth, y1 - eraserWidth))
        xy1List.add(Pair(x1 - eraserWidth, y1 + eraserWidth))
        xy1List.add(Pair(x1 + eraserWidth, y1 + eraserWidth))
        xy1List.add(Pair(x1, y1))

        val xy2List = arrayListOf<Pair<Float, Float>>()
        xy2List.add(Pair(x2 - eraserWidth, y2 - eraserWidth))
        xy2List.add(Pair(x2 + eraserWidth, y2 - eraserWidth))
        xy2List.add(Pair(x2 - eraserWidth, y2 + eraserWidth))
        xy2List.add(Pair(x2 + eraserWidth, y2 + eraserWidth))
        xy2List.add(Pair(x2, y2))

        //check if coordinates are inside border of previous commands
        val commandHistory = canvasRepository.getDrawCommandHistory().value ?: return
//        var eraserTarget:ArrayList<CanvasCommand> = arrayListOf()
        commandHistory.forEach {
            val pathDataPoints = it.value.pathDataPoints

            if (it.value.type == CommandType.DRAW && !pathDataPoints.isNullOrEmpty() && !it.value.isErased) {
                var bonusOffset =it.value.paint?.strokeWidth ?: 0f
                bonusOffset /= 2f
                var isContact = false
                for (n in 0 until pathDataPoints.size - 1) {
                    for (i in 0..4) {
                        val x1 = xy1List.get(i).first-bonusOffset
                        val y1 = xy1List.get(i).second-bonusOffset
                        val x2 = xy2List.get(i).first+bonusOffset
                        val y2 = xy2List.get(i).second+bonusOffset
                        //calculate  t = (q − p) × s / (r × s) https://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
                        val r = Pair(x2 - x1, y2 - y1) // P+t*R
                        val s = Pair(
                            pathDataPoints[n + 1].x - pathDataPoints[n].x,
                            pathDataPoints[n + 1].y - pathDataPoints[n].y
                        ) // Q+u*S
                        val productRS = r.first * s.second - r.second * s.first // r x s
                        val divS = Pair(s.first / productRS, s.second / productRS) //  s / (r x s)
                        val subQP = Pair(pathDataPoints[n].x - x1, pathDataPoints[n].y - y1) // q-p
                        val tResult = subQP.first * divS.second - subQP.second * divS.first
                        val t = tResult.toDouble()

                        // u = (q − p) × r / (r × s)
                        val divR = Pair(r.first / productRS, r.second / productRS)
                        val uResult = subQP.first * divR.second - subQP.second * divR.first
                        val u = uResult.toDouble()
                        Log.d("u", tResult.toString())

                        if (t.compareTo(0.0) >= 0 && t.compareTo(1.0) <= 0 &&
                            u.compareTo(0.0) >= 0 && u.compareTo(1.0) <= 0
                        ) {
                            isContact = true
                        }
                    }
                    //check if there is a point inside
                    if (xy1List.get(0).first <= pathDataPoints[n].x && pathDataPoints[n].x <= xy1List.get(
                            3
                        ).first
                        && xy1List.get(0).second <= pathDataPoints[n].y && pathDataPoints[n].y <= xy1List.get(
                            3
                        ).second
                    )
                        isContact = true

                    if (xy2List.get(0).first <= pathDataPoints[n].x && pathDataPoints[n].x <= xy2List.get(
                            3
                        ).first
                        && xy2List.get(0).second <= pathDataPoints[n].y && pathDataPoints[n].y <= xy2List.get(
                            3
                        ).second
                    )
                        isContact = true


                }
                if (isContact) {
                    canvasRepository.eraseCommand(it.key)
                    gameSessionRepository.sendManualCommand(CommandType.ERASE, it.key)
                }


            }
        }

    }

    fun getIsCanvasLocked() = gameSessionRepository.getIsCanvasLocked()


    fun getStrokeWidth() = drawingParametersRepository.getStrokeWidth()
    fun getCellWidthGrid() = drawingParametersRepository.getCellWidthGrid()
    fun getEraser() = drawingParametersRepository.getErase()
    fun getGridVal() = drawingParametersRepository.getGrid()

    fun getShouldShowWord() = gameSessionRepository.getShouldShowWord()

}