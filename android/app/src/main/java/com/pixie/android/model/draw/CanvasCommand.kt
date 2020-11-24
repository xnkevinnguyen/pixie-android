package com.pixie.android.model.draw

import android.graphics.Paint

data class CanvasCommand (
    var type: CommandType,
    val paint: Paint?=null,
    val pathDataPoints:ArrayList<SinglePoint>?=null,
    val reference:CanvasCommand? = null, //Use for eraser to point to target command,
    var isErased:Boolean = false,
    val potracePoints: ArrayList<PotraceDataPoint>?=null
)


enum class CommandType{
    DRAW,
    DRAW_POTRACE,
    ERASE,
    //Undo is also used when someone else erases (hide an element)
    UNDO,
    // Show an element previously hidden
    REDO
}

