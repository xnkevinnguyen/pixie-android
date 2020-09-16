package com.pixie.android.ui.draw.drawTools

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.pixie.android.data.draw.DrawingParametersRepository
import kotlin.random.Random

class DrawToolsViewModel(private val drawingParametersRepository: DrawingParametersRepository):ViewModel() {

    fun getPrimaryColor() = drawingParametersRepository.getPrimaryDrawingColor()

    // TODO Logic to let user pick the color
    fun modifyPrimaryColor(){
        val newColor = Color.valueOf(Color.argb(255, Random.nextInt(256),Random.nextInt(256), Random.nextInt(256)))
        drawingParametersRepository.setPrimaryDrawingColor(newColor)
    }
    fun undo(){
        drawingParametersRepository.popLastDrawCommandFromHistory()
    }
}