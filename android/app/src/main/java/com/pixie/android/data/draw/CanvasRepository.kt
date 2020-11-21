package com.pixie.android.data.draw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.*
import com.pixie.android.type.PathStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.HashMap

class CanvasRepository {
    private var drawCommandHistory = MutableLiveData<HashMap<Double, CanvasCommand>>()
    private var previousID: Double = 0.0

    fun getDrawCommandHistory(): LiveData<HashMap<Double, CanvasCommand>> {
        return drawCommandHistory
    }


    fun clear() {
        drawCommandHistory.postValue(hashMapOf())

    }

    fun eraseCommand(commandID: Double) {
        drawCommandHistory.value?.get(commandID)?.type = CommandType.ERASE
        drawCommandHistory.notifyObserver()

    }


    // Add Draw command should not notify the observer
    fun addCanvasCommand(id: Double, drawCommand: CanvasCommand) {
        if (drawCommandHistory.value == null) {
            drawCommandHistory.postValue(hashMapOf(Pair(id, drawCommand)))
        } else {
            drawCommandHistory.value?.put(id, drawCommand)
            drawCommandHistory.notifyObserver()
        }

    }
    // used only for virtual player drawing
    fun appendCanvasCommand(id:Double,drawCommand: CanvasCommand){
        if (drawCommandHistory.value != null && !drawCommand.path.isNullOrEmpty()) {

            val currentCommand = drawCommandHistory.value?.get(id)

            var insertionPoint =1
            currentCommand?.path?.forEachIndexed {index, point->
                val orderID=drawCommand.path[0].orderID
                if(orderID !=null && point.orderID!=null&& point.orderID<orderID){
                    insertionPoint = index+1
                }
            }

            currentCommand?.path?.addAll(insertionPoint,drawCommand.path.toList())
            drawCommandHistory.notifyObserver()
        }else if(drawCommandHistory.value != null && !drawCommand.potracePoints.isNullOrEmpty()){
            val currentCommand = drawCommandHistory.value?.get(id)
            if(currentCommand == null){
                drawCommandHistory.value?.put(id, drawCommand)
            }else {
                var insertionPoint =1
                currentCommand.potracePoints?.forEachIndexed {index, point->
                    if(point.orderID<drawCommand.potracePoints[0].orderID){
                        insertionPoint = index+1
                    }
                }
                currentCommand.potracePoints?.addAll(insertionPoint,drawCommand.potracePoints.toList())
            }
            drawCommandHistory.notifyObserver()

        }else if(drawCommandHistory.value.isNullOrEmpty()){
            addCanvasCommand(id,drawCommand)
        }
    }

    // Receiver Manual draw add a point
    fun addManualDrawPoint(point: ManualDrawingPoint) {
        if (point.status.equals(PathStatus.BEGIN)) {
            addNewManualDrawPoint(point)
        } else {
            updateCommand(point)
        }
    }

    //add new point
    fun addNewManualDrawPoint(point: ManualDrawingPoint) {
        val firstPathPoint = PathPoint(point.x, point.y, point.x, point.y)
        val command = CanvasCommand(CommandType.DRAW, point.paint, arrayListOf(firstPathPoint))
        addCanvasCommand(point.pathID, command)
        previousID = point.pathID

    }

    //update an existing command with a new point
    fun updateCommand(point: ManualDrawingPoint) {
        val currentCommand = drawCommandHistory.value?.get(point.pathID)

        if (currentCommand != null && currentCommand.path?.last() != null && point.pathID == previousID) {
            val pathPoint = PathPoint(
                currentCommand.path.last().x2, currentCommand.path.last().y2,
                point.x, point.y
            )
            currentCommand.path.add(pathPoint)
            drawCommandHistory.notifyObserver()

        } else {
            Log.d("ManualDrawingError", "ManualDrawing updates a point to a non-existent command")
        }
    }

    // Sender and Receiver for UNDO & REDO
    fun handleServerDrawHistoryCommand(serverCommand: ServerDrawHistoryCommand) {
        Log.d("CanvasRepository", "Received Command from server " + serverCommand.commandType)
        CoroutineScope(Dispatchers.Main).launch {
            if (serverCommand.commandType == CommandType.ERASE) {
                drawCommandHistory.value?.get(serverCommand.commandPathID)?.type = CommandType.ERASE
            }
            else if (serverCommand.commandType == CommandType.REDO || serverCommand.commandType == CommandType.UNDO) {
                if (drawCommandHistory.value?.get(serverCommand.commandPathID)?.type == CommandType.DRAW) {
                    drawCommandHistory.value?.get(serverCommand.commandPathID)?.type =
                        serverCommand.commandType
                } else {
                    drawCommandHistory.value?.get(serverCommand.commandPathID)?.type =
                        CommandType.DRAW
                }
            }
            drawCommandHistory.notifyObserver()
        }

    }


    // Singleton
    companion object {
        @Volatile
        private var instance: CanvasRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: CanvasRepository().also {
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