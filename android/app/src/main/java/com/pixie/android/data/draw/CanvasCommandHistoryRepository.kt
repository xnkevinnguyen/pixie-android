package com.pixie.android.data.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.CommandType
import com.pixie.android.model.draw.ManualDrawingPoint
import com.pixie.android.model.draw.ServerDrawHistoryCommand
import java.util.*

class CanvasCommandHistoryRepository {
    private var drawCommandHistory = MutableLiveData<MutableList<CanvasCommand>>()
    private var undoneCommandList: Stack<CanvasCommand> = Stack()


    fun getDrawCommandHistory(): LiveData<MutableList<CanvasCommand>> {
        return drawCommandHistory
    }

    fun resetDrawCommandHistory() {
        val count = drawCommandHistory.value?.count()
        if(count != null && count > 0) {
            drawCommandHistory = MutableLiveData<MutableList<CanvasCommand>>()
        }
        restoreUndoneCommandList()
    }
    fun clear(){
//        resetDrawCommandHistory()

//        drawCommandHistory.notifyObserver()
        drawCommandHistory.postValue(mutableListOf())
    }
    //undo
    fun popLastDrawCommandFromHistory() {
        val count = drawCommandHistory.value?.count()
        if (count != null && count > 0) {
            val commandToRemove = drawCommandHistory.value!!.removeAt(count - 1)
            if(commandToRemove.type == CommandType.ERASE){
                commandToRemove.reference?.isErased=false
            }
            addUndoneCommand(commandToRemove)
            drawCommandHistory.notifyObserver()
        }
    }

    fun popUndoneCommand() {
        if (!undoneCommandList.empty()) {
            val commandToRedo = undoneCommandList.pop()
            addCanvasCommand(commandToRedo)
            if(commandToRedo.type==CommandType.ERASE){
                commandToRedo.reference?.isErased = true
            }
            drawCommandHistory.notifyObserver()
        }
    }

    // Add Draw command should not notify the observer
    fun addCanvasCommand(drawCommand:CanvasCommand) {
        if (drawCommandHistory.value == null) {
            drawCommandHistory.postValue(arrayListOf(drawCommand))
        } else {
            drawCommandHistory.value?.add(drawCommand)
            //erase needs a reload
//            if(drawCommand.type == CommandType.ERASE)
                drawCommandHistory.notifyObserver()
        }

    }

    // Receiver Manual draw add a point
    fun addManualDrawPoint(point:ManualDrawingPoint){

    }
    // Sender and Receiver
    fun handleServerDrawHistoryCommand(serverCommand:ServerDrawHistoryCommand){

    }

    // Should not notify the observer
    fun addUndoneCommand(command: CanvasCommand) {
        undoneCommandList.add(command)
    }

    // Restore undoneCommandList
    fun restoreUndoneCommandList() {
        if (!undoneCommandList.isNullOrEmpty()) {
            undoneCommandList.clear()
        }
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