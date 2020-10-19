package com.pixie.android.ui.draw.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.PathPoint


class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Holds the path you are currently drawing.
    private var path = Path()
    var drawStroke: Float = 12f
    var pathData = ArrayList<PathPoint>()
    private lateinit var canvasViewModel: CanvasViewModel


    var drawColor: Int = 0 // Should be replaced at runtime with default BLACK value from repository
    private val backgroundColor = Color.TRANSPARENT
    private var erase = false

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

    private var paint = generatePaint()

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f


    fun setErase(isErase: Boolean) {
        erase = isErase
        if (erase) paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        else paint.xfermode = null
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
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth =
            drawStroke
    }

    fun drawFromCommandList(canvasCommandList: List<CanvasCommand>) {

        canvas.drawColor(backgroundColor, PorterDuff.Mode.CLEAR)
        canvasCommandList.forEach {
            if (it.paint != null && it.path != null) {
                val pathToDraw = Path()
                pathToDraw.moveTo(it.path.first().x1, it.path.first().y1)
                it.path.forEach {
                    pathToDraw.quadTo(it.x1, it.y1, it.x2, it.y2)

                }
                canvas.drawPath(pathToDraw, it.paint)
            }
        }
        invalidate()
    }


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (::bitmap.isInitialized) bitmap.recycle()
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)


    }

    override fun onDraw(canvas: Canvas) {
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y
        if(!erase){//draw actions
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart()
            MotionEvent.ACTION_MOVE -> onTouchMove()
            MotionEvent.ACTION_UP -> onTouchStop()
        }
        }else if(erase){
            when(event.action){
                MotionEvent.ACTION_DOWN ->onEraseStart()
                MotionEvent.ACTION_MOVE ->onEraseMove()
            }
        }
        return true
    }
    private fun onEraseStart(){
//        canvasViewModel.captureEraseAction(motionTouchEventX,motionTouchEventY,)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }
    private fun onEraseMove(){
        canvasViewModel.captureEraseAction(currentX,currentY,motionTouchEventX,motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun onTouchStart() {
        pathData.clear()
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
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
        canvas.drawPath(path, paint)

        // Invalidate triggers onDraw from the view
        invalidate()

    }

    private fun onTouchStop() {
        if (!erase) {
            canvasViewModel.addCommandToHistory(Paint(paint), ArrayList(pathData))
        }
        pathData.clear()
        path.reset()
    }
}