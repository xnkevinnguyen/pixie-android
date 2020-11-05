package com.pixie.android.data.draw

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.*
import com.pixie.android.type.PathStatus
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random

class CanvasCommandHistoryRepository {
    private var drawCommandHistory = MutableLiveData<HashMap<Double,CanvasCommand>>()
    private var previousID:Double = 0.0

    fun getDrawCommandHistory(): LiveData<HashMap<Double,CanvasCommand>> {
        return drawCommandHistory
    }


    fun clear(){

        Log.d("CanvasCommandHistoryRepository","clear")
            drawCommandHistory.postValue(hashMapOf())
            drawCommandHistory.notifyObserver()
//        }

    }


    // Add Draw command should not notify the observer
    fun addCanvasCommand(id:Double,drawCommand:CanvasCommand) {
        Log.d("CanvasCommandHistoryRepository","addCanvasCommand")

        if (drawCommandHistory.value == null) {
            drawCommandHistory.postValue(hashMapOf(Pair(id,drawCommand)))
        } else {
            drawCommandHistory.value?.put(id,drawCommand)
                drawCommandHistory.notifyObserver()
        }

    }

    // Receiver Manual draw add a point
    fun addManualDrawPoint(point:ManualDrawingPoint){
        if(point.status.equals(PathStatus.BEGIN)){
            addNewManualDrawPoint(point)
        }else{
            updateCommand(point)
        }
    }
    //add new point
    fun addNewManualDrawPoint(point:ManualDrawingPoint){
        val firstPathPoint = PathPoint(point.x,point.y,point.x,point.y)
        val command = CanvasCommand(CommandType.DRAW, point.paint, arrayListOf(firstPathPoint))
        addCanvasCommand(point.pathID,command)
        previousID = point.pathID

    }

    //update an existing command with a new point
    fun updateCommand(point:ManualDrawingPoint){
        val currentCommand = drawCommandHistory.value?.get(point.pathID)

        if(currentCommand!=null && currentCommand.path?.last()!=null && point.pathID == previousID){
            val pathPoint = PathPoint(currentCommand.path.last().x2, currentCommand.path.last().y2,
            point.x,point.y)
            currentCommand.path.add(pathPoint)
            drawCommandHistory.notifyObserver()
            Log.d("CanvasCommandHistoryRepository","updateCommand")

        }else{
            Log.d("ManualDrawingError","ManualDrawing updates a point to a non-existent command")
        }
    }

    // Sender and Receiver for UNDO & REDO
    fun handleServerDrawHistoryCommand(serverCommand:ServerDrawHistoryCommand){

    }



    // Singleton
    companion object {
        @Volatile
        private var instance: CanvasCommandHistoryRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: CanvasCommandHistoryRepository().also {
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