package com.pixie.android.data.draw

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.DrawCommand
import java.util.*

class DrawingParametersRepository {


    // non persistent attribute
    private var primaryDrawingColor =
        MutableLiveData<Color>().apply { postValue(Color.valueOf(Color.BLACK)) }
    private var strokeWidth = MutableLiveData<Float>().apply { postValue(12F) }
    private var cellWidthGrid = MutableLiveData<Int>().apply { postValue(25) }
    private var erase = MutableLiveData<Boolean>().apply { postValue(false) }
    private var grid = MutableLiveData<Boolean>().apply { postValue(false) }



    fun getPrimaryDrawingColor(): LiveData<Color> {
        return primaryDrawingColor;
    }

    fun setPrimaryDrawingColor(newColor: Color) {
        primaryDrawingColor.postValue(newColor)
    }

    fun getStrokeWidth(): LiveData<Float> {
        return strokeWidth;
    }

    fun setCellWidthGrid(width: Int) {
        cellWidthGrid.postValue(width)
    }

    fun getCellWidthGrid(): LiveData<Int> {
        return cellWidthGrid;
    }

    fun setStrokeWidth(size: Float) {
        strokeWidth.postValue(size)
    }

    fun getErase(): LiveData<Boolean> {
        return erase;
    }

    fun setErase(useErase: Boolean) {
        erase.postValue(useErase)
    }

    fun getGrid(): LiveData<Boolean> {
        return grid;
    }

    fun setGrid(gridOn: Boolean) {
        grid.postValue(gridOn)

    }


    // Singleton
    companion object {
        @Volatile
        private var instance: DrawingParametersRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: DrawingParametersRepository().also {
                instance = it
            }
        }
    }
}