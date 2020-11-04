package com.pixie.android.model.draw

import android.graphics.Paint
import com.pixie.android.type.PathStatus

data class ManualPathPointInput (val x:Float,  val y :Float,
 val pathStatus:PathStatus, val paint: Paint)
