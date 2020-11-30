package com.pixie.android.ui.draw.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.alpha
import com.pixie.android.R
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.PathPoint
import com.pixie.android.model.draw.PotraceCommand
import com.pixie.android.model.draw.SinglePoint
import com.pixie.android.type.PathStatus


class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Holds the path you are currently drawing.
    private var path = Path()
    var drawStroke: Float = 12f
    var pathData = ArrayList<PathPoint>()
    private lateinit var canvasViewModel: CanvasViewModel


    var drawColor: Int = 0 // Should be replaced at runtime with default BLACK value from repository
    private val backgroundColor = Color.TRANSPARENT
    private var erase = false

    private var canvas: Canvas? = null
    private lateinit var bitmap: Bitmap

    private var paint = generatePaint()

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var onDrawingMove: Boolean = false
    private var isCanvasLocked: Boolean = false
    private var refCommandList:List<CanvasCommand>?=null


    fun setIsCanvasLocked(isLocked: Boolean) {
        isCanvasLocked = isLocked
    }


    fun setErase(isErase: Boolean) {
        erase = isErase
    }

    fun setViewModel(viewModel: CanvasViewModel) {
        canvasViewModel = viewModel
    }

    fun reinitializeDrawingParameters() {
        // Refresh paint with latest parameters
        paint = generatePaint()
    }

    fun generatePaint(): Paint = Paint().apply {
        color = drawColor
        alpha = drawColor.alpha
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth =
            drawStroke
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }

    fun drawFromCommandList(canvasCommandList: List<CanvasCommand>) {
        refCommandList=canvasCommandList
        if (canvas != null) {
            canvas?.drawColor(backgroundColor, PorterDuff.Mode.CLEAR)
            canvasCommandList.forEach {
                if (it.paint != null && !it.pathDataPoints.isNullOrEmpty()) {
                    val pathToDraw = Path()
                    pathToDraw.moveTo(it.pathDataPoints.first().x, it.pathDataPoints.first().y)
                    val length = it.pathDataPoints.size
//                    it.pathDataPoints.forEach {
//                        pathToDraw.quadTo(it.x1, it.y1, it.x2, it.y2)
//
//                    }
                    if (length > 1) {
                        for (n in 0 until length - 1) {
                            pathToDraw.quadTo(
                                it.pathDataPoints[n].x, it.pathDataPoints[n].y,
                                it.pathDataPoints[n + 1].x, it.pathDataPoints[n + 1].y
                            )
                        }
                    } else {
                        pathToDraw.quadTo(
                            it.pathDataPoints[0].x, it.pathDataPoints[0].y,
                            it.pathDataPoints[0].x, it.pathDataPoints[0].y
                        )
                    }
                    canvas?.drawPath(pathToDraw, it.paint)
                }
            }
            invalidate()
        } else {
            Log.d("CanvasError", "Canvas is null")
        }
    }

    fun drawFromCommandListPotrace(canvasCommandList: List<CanvasCommand>) {
        if (canvas != null) {
            canvas?.drawColor(backgroundColor, PorterDuff.Mode.CLEAR)
            canvasCommandList.forEach { command ->
                if (command.paint != null && !command.potracePoints.isNullOrEmpty()) {

                    val pathToDraw = Path()
                    pathToDraw.fillType = Path.FillType.WINDING

                    command.potracePoints.forEach {
                        val secondaryCoordinates = it.secondaryCoordinates
                        if (it.command.equals(PotraceCommand.M)) {
                            pathToDraw.moveTo(it.primaryCoordinates.x, it.primaryCoordinates.y)
                        } else if (it.command.equals(PotraceCommand.C) && secondaryCoordinates != null) {

                            pathToDraw.cubicTo(
                                secondaryCoordinates.x1,
                                secondaryCoordinates.y1,
                                secondaryCoordinates.x2,
                                secondaryCoordinates.y2,
                                it.primaryCoordinates.x,
                                it.primaryCoordinates.y
                            )
                        } else if (it.command.equals(PotraceCommand.L)) {
                            pathToDraw.lineTo(it.primaryCoordinates.x, it.primaryCoordinates.y)
                        }

                    }
                    canvas?.drawPath(pathToDraw, command.paint)
                }
            }
            invalidate()
        } else {
            Log.d("CanvasError", "Canvas is null")
        }
    }


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas?.drawColor(backgroundColor)


    }

    override fun onDraw(canvas: Canvas) {
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isCanvasLocked) {
            motionTouchEventX = event.x
            motionTouchEventY = event.y
            if (!erase) {//draw actions
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> onTouchStart()
                    MotionEvent.ACTION_MOVE -> onTouchMove()
                    MotionEvent.ACTION_UP -> onTouchStop()
                }
            } else if (erase) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> onEraseStart()
                    MotionEvent.ACTION_MOVE -> onEraseMove()
                    MotionEvent.ACTION_UP->onEraseUp()
                }
            }
        }
        return true
    }

    private fun onEraseStart() {

        paint.style= Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth= 3f
        canvasViewModel.captureEraseActionTouch(motionTouchEventX,motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY


        pathData.clear()
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
        val tempPaint = Paint().apply {
            style= Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth= 3f
        }

        path.addRect(motionTouchEventX-5, motionTouchEventY-5,motionTouchEventX+5,motionTouchEventY+5,Path.Direction.CW)
        canvas?.drawPath(path, tempPaint)

    }

    private fun onEraseMove() {
        val ref = refCommandList?.toMutableList()
        if(!ref.isNullOrEmpty()){
            drawFromCommandList(ref)
        }else{
            drawFromCommandList(arrayListOf())
        }
        path.reset()
        val tempPaint = Paint().apply {
            style= Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth= 3f
        }

        canvasViewModel.captureEraseAction(currentX, currentY, motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY

        path.addRect(motionTouchEventX-10, motionTouchEventY-10,motionTouchEventX+10,motionTouchEventY+10,Path.Direction.CW)

        canvas?.drawPath(path, tempPaint)
        invalidate()

    }
    private  fun onEraseUp(){
        //erase erase border
        val ref = refCommandList?.toMutableList()
        if(!ref.isNullOrEmpty()){
            drawFromCommandList(ref)
        }else{
            drawFromCommandList(arrayListOf())
        }
    }
    private fun onTouchStart() {
        pathData.clear()
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
        canvasViewModel.sendPoint(currentX, currentY, PathStatus.BEGIN, Paint(paint))
        pathData.add(
            PathPoint(
                currentX,
                currentY,
                motionTouchEventX,
                motionTouchEventY
            )
        )
        onDrawingMove = true

    }


    private fun onTouchMove() {
        pathData.add(
            PathPoint(
                currentX,
                currentY,
                motionTouchEventX,
                motionTouchEventY
            )
        )
        path.quadTo(
            currentX,
            currentY,
            motionTouchEventX,
            motionTouchEventY
        )

        currentX = motionTouchEventX
        currentY = motionTouchEventY
        canvas?.drawPath(path, paint)
        // To prevent an even after ontouch stop
        if (onDrawingMove)
            canvasViewModel.sendPoint(currentX, currentY, PathStatus.ONGOING, Paint(paint))

        // Invalidate triggers onDraw from the view
        invalidate()

    }

    private fun onTouchStop() {
        onDrawingMove = false
        if (!erase && !pathData.isNullOrEmpty()) {

            var pathDataPoints =
                arrayListOf<SinglePoint>(SinglePoint(pathData.first().x1, pathData.first().y1, 0.0))
            pathData.forEachIndexed { index, pathPoint ->
                pathDataPoints.add(SinglePoint(pathPoint.x2, pathPoint.y2, index.toDouble()))
            }
            // send point
            canvasViewModel.sendFinalPoint(
                currentX,
                currentY,
                PathStatus.END,
                Paint(paint),
                pathDataPoints
            )
//            canvasViewModel.addCommandToHistory(Paint(paint), ArrayList(pathData))
        }
        pathData.clear()
        path.reset()
    }
}