package com.pixie.android.model.draw

import android.graphics.Paint

data class CanvasCommand (
    var type: CommandType,
    val paint: Paint?=null,
    val path:ArrayList<PathPoint>?=null,
    val reference:CanvasCommand? = null, //Use for eraser to point to target command,
    var isErased:Boolean = false
)

data class Border( val xMin:Float, val yMin:Float, val xMax:Float,val yMax:Float)
enum class CommandType{
    DRAW,
    ERASE,
    //Undo is also used when someone else erases (hide an element)
    UNDO,
    // Show an element previously hidden
    REDO
}
