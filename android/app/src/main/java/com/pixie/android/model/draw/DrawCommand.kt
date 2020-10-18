package com.pixie.android.model.draw

import android.graphics.Paint
import android.graphics.Path

data class DrawCommand (
    val path:ArrayList<PathPoint>,
    val paint: Paint
)