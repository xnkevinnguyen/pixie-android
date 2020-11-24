package com.pixie.android.model.draw

data class PotraceDataPoint(
    val orderID:Double,
    val command: PotraceCommand, val primaryCoordinates:SinglePoint, var secondaryCoordinates: PathPoint?=null,
    var xAxisRotation: Double? = null, var largeArc: Double? = null, var sweep: Double? = null
)



enum class PotraceCommand {
    M,
    C,
    L
}