package com.pixie.android.data

import android.graphics.Paint
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pixie.android.data.draw.CanvasRepository
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.CommandType
import com.pixie.android.model.draw.ManualDrawingPoint
import com.pixie.android.type.PathStatus
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


class CanvasRepositoryTest {
    // Rule used to test MutableLiveData https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()
    @Test
    fun testAddDrawCommandOnceShouldReturnListofOne(){
        //PREPARE
        val repository:CanvasRepository = getRepository()

        //PERFORM
        val drawCommand = CanvasCommand( CommandType.DRAW, Paint())
        repository.addCanvasCommand(1000000.0,drawCommand)
        val result = repository.getDrawCommandHistory()


        //CHECK
        Assert.assertEquals(result.value!!.size,1)
    }
    @Test
    fun testClearDrawCommandHistory_ShouldEmptiesHistory(){
        //PREPARE
        val repository:CanvasRepository = getRepository()
        val drawCommand = CanvasCommand( CommandType.DRAW, Paint())
        repository.addCanvasCommand(1000000.0,drawCommand)
        //PERFORM
        repository.clear()
        val result = repository.getDrawCommandHistory()


        //CHECK
        Assert.assertTrue(result.value?.isNullOrEmpty()==true)

    }

    @Test
    fun testEraseCommand_ShouldChangeCommandType(){
        //PREPARE
        val repository:CanvasRepository = getRepository()
        val drawCommand = CanvasCommand( CommandType.DRAW, Paint())
        val commandID = 10000000.0
        repository.addCanvasCommand(commandID,drawCommand)
        //PERFORM
        repository.eraseCommand(commandID)

        //CHECK
        Assert.assertEquals(repository.getDrawCommandHistory().value?.get(commandID)?.type,CommandType.ERASE)

    }

    @Test
    fun testAddManualDrawPoint_ShouldAddCommand(){
        //PREPARE
        val repository:CanvasRepository = getRepository()
        val pointToAdd = ManualDrawingPoint(1000000.0,1.0,1f,1f,PathStatus.BEGIN)
        //PERFORM

        repository.addManualDrawPoint(pointToAdd)
        val result = repository.getDrawCommandHistory()

        //CHECK
        Assert.assertEquals(result.value!!.size,1)

    }

    @Test
    fun testAddManualDrawPoint_ShouldUpdateCommand(){
        //PREPARE
        val repository:CanvasRepository = getRepository()
        val pointToAdd = ManualDrawingPoint(1000000.0,1.0,1f,1f,PathStatus.BEGIN)
        val pointToAdd2 = ManualDrawingPoint(1000000.0,2.0,1f,1f,PathStatus.ONGOING)
        //PERFORM

        repository.addManualDrawPoint(pointToAdd)
        repository.addManualDrawPoint(pointToAdd2)

        val result = repository.getDrawCommandHistory()
        val commandSize=result.value?.get(pointToAdd.pathID)?.pathDataPoints?.size
        //CHECK
        Assert.assertEquals(result.value!!.size,1)
        Assert.assertEquals(commandSize,2)

    }
    @Test
    fun test(){
        //PREPARE
        //PERFORM
        //CHECK
    }

    private fun getRepository():CanvasRepository{
        return CanvasRepository()
    }
}