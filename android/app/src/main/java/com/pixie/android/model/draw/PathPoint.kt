package com.pixie.android.model.draw

data class PathPoint(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float,
    val orderID:Double?=null
)

data class SinglePoint(
    val x:Float,
    val y:Float,
    val orderID:Double
)