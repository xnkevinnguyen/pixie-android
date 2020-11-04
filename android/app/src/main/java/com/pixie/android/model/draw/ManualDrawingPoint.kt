package com.pixie.android.model.draw

import android.graphics.Paint
import com.pixie.android.type.CommandStatus
import com.pixie.android.type.PathStatus

data class ManualDrawingPoint (
    val x:Float = 0f,
    val y :Float = 0f,
    val status: PathStatus,
    val commandPathID: Double,
    val commandType: CommandType,
    val paint: Paint?=null
)