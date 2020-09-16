package com.pixie.android.ui.draw.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class GridView(context: Context, attrs: AttributeSet) : View(context,attrs) {

    private var numColumns:Int = 0
    private var numRows:Int = 0
    private var cellWidth = 25
    private val blackPaint: Paint = Paint()

    fun setCellWidth(width: Int) {
        this.cellWidth = width
        calculateDimensions()
    }

    private fun getCellWidth(): Int {
        return cellWidth
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateDimensions()
    }

    private fun calculateDimensions() {
        if (cellWidth == 0) {
            numColumns = 0
            numRows = 0
        }
        numColumns = width / cellWidth
        numRows = height / cellWidth
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        if (numColumns == 0 || numRows == 0) {
            return
        }

        for (i in 1 until numColumns + 1) {
            canvas.drawLine((i * cellWidth).toFloat(), 0F,
                (i * cellWidth).toFloat(), height.toFloat(), blackPaint)
        }
        for (i in 1 until numRows + 1) {
            canvas.drawLine(
                0F, (i * cellWidth).toFloat(),
                width.toFloat(), (i * cellWidth).toFloat(), blackPaint)
        }
    }
}