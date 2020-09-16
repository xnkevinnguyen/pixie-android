package com.pixie.android.ui.draw.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import com.pixie.android.R
import com.pixie.android.model.draw.DrawCommand


private const val STROKE_WIDTH: Float = 12f

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Holds the path you are currently drawing.
    private var path = Path()

    // Will trigger an event to signal a path has been added
    var completedCommand = MutableLiveData<DrawCommand>()
    private var history: MutableList<Path> = arrayListOf()
    var drawColor: Int = 0 // Should be replaced at runtime with default BLACK value from repository
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private lateinit var canvas: Canvas
    private lateinit var bitmap: Bitmap

    private var paint = generatePaint()

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

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
            STROKE_WIDTH
    }

    fun drawFromCommandList(drawCommandList: List<DrawCommand>) {

        canvas.drawColor(backgroundColor)
        drawCommandList.forEach {
            canvas.drawPath(it.path, it.paint)
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

        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart()
            MotionEvent.ACTION_MOVE -> onTouchMove()
            MotionEvent.ACTION_UP -> onTouchStop()
        }
        return true
    }

    private fun onTouchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun onTouchMove() {
        path.quadTo(
            currentX,
            currentY,
            (motionTouchEventX + currentX) / 2,
            (motionTouchEventY + currentY) / 2
        )
        currentX = motionTouchEventX
        currentY = motionTouchEventY
        canvas.drawPath(path, paint)

        // Invalidate triggers onDraw from the view
        invalidate()

    }

    private fun onTouchStop() {
        completedCommand.postValue(DrawCommand(Path(path), paint))
        path.reset()
    }
}