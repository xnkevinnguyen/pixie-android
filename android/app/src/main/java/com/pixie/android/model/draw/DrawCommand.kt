package com.pixie.android.model.draw

import android.graphics.Paint
import android.graphics.Path

data class DrawCommand (
    val path:Path,
    val paint: Paint
)