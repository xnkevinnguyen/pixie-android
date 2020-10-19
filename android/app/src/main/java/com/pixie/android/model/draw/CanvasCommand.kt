package com.pixie.android.model.draw

import android.graphics.Paint

data class CanvasCommand (
    val type: CommandType,
    val paint: Paint?=null,
    val path:ArrayList<PathPoint>?=null,
    val border:Border?=null,
    val reference:CanvasCommand? = null, //Use for eraser to point to target command,
    var isErased:Boolean = false
)

data class Border( val xMin:Float, val yMin:Float, val xMax:Float,val yMax:Float)
enum class CommandType{
    DRAW,
    ERASE,
    UNDO,
    REDO
}
