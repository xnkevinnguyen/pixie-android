package com.pixie.android.data.draw

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DrawingParametersRepository {

    // non persistent attribute
    private var  primaryDrawingColor = MutableLiveData<Color>().apply{postValue(Color.valueOf(Color.BLACK))}

    public fun getPrimaryDrawingColor():LiveData<Color>{
        return primaryDrawingColor;
    }

    public fun setPrimaryDrawingColor( newColor: Color){
        primaryDrawingColor.postValue(newColor)
    }

    // Singleton
    companion object{
        @Volatile private var instance:DrawingParametersRepository? = null
        fun getInstance() = instance?: synchronized(this){
            instance?: DrawingParametersRepository().also{
                instance = it
            }
        }
    }
}