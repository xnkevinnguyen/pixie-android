package com.pixie.android.data.draw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pixie.android.model.draw.DrawCommand
import java.util.*

class DrawCommandHistoryRepository {
    private var drawCommandHistory = MutableLiveData<MutableList<DrawCommand>>()
    private var undoneCommandList: Stack<DrawCommand> = Stack()


    fun getDrawCommandHistory(): LiveData<MutableList<DrawCommand>> {
        return drawCommandHistory
    }

    fun popLastDrawCommandFromHistory() {
        val count = drawCommandHistory.value?.count()
        if (count != null && count > 0) {
            val commandToRemove = drawCommandHistory.value!!.removeAt(count - 1)
            addUndoneCommand(commandToRemove)
            drawCommandHistory.notifyObserver()
        }
    }

    fun popUndoneCommand() {
        if (!undoneCommandList.empty()) {
            val commandToRedo = undoneCommandList.pop()
            addDrawCommand(commandToRedo)
            drawCommandHistory.notifyObserver()
        }
    }


    // Add Draw command should not notify the observer
    fun addDrawCommand(drawCommand: DrawCommand) {
        if (drawCommandHistory.value == null) {
            drawCommandHistory.postValue(arrayListOf(drawCommand))

        } else {
            drawCommandHistory.value?.add(drawCommand)
        }

    }

    // Should not notify the observer
    fun addUndoneCommand(command: DrawCommand) {
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
        private var instance: DrawCommandHistoryRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: DrawCommandHistoryRepository().also {
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