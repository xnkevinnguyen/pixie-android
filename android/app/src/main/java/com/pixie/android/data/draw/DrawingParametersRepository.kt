package com.pixie.android.data.draw

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.DrawCommand
import java.util.*

class DrawingParametersRepository {

    // non persistent attribute
    private var  primaryDrawingColor = MutableLiveData<Color>().apply{postValue(Color.valueOf(Color.BLACK))}
    private var drawCommandHistory = MutableLiveData<MutableList<DrawCommand>>()
    private var undoneCommandList = MutableLiveData<MutableList<DrawCommand>>()

    public fun getPrimaryDrawingColor():LiveData<Color>{
        return primaryDrawingColor;
    }

    public fun setPrimaryDrawingColor( newColor: Color){
        primaryDrawingColor.postValue(newColor)
    }
    public fun getDrawCommandHistory():LiveData<MutableList<DrawCommand>>{
        return drawCommandHistory
    }
    public fun popLastDrawCommandFromHistory() {
        val count = drawCommandHistory.value?.count()
        if (count != null) {
            drawCommandHistory.value?.removeAt(count-1)
        }
        drawCommandHistory.notifyObserver()
    }

    public fun addDrawCommand(drawCommand: DrawCommand){
        if (drawCommandHistory.value == null){
            drawCommandHistory.postValue(arrayListOf(drawCommand))

        }else {
            drawCommandHistory.value?.add(drawCommand)
        }

//        drawCommandHistory.notifyObserver()
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
    // Function to make sure observer is notified when a data structure is modified
    // https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}