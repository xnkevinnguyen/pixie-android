package com.pixie.android.data

import android.graphics.Paint
import android.graphics.Path
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pixie.android.data.draw.CanvasCommandHistoryRepostiroy
import com.pixie.android.model.draw.CanvasCommand
import com.pixie.android.model.draw.CommandType
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


class CanvasCommandHistoryRepositoryTest {
    // Rule used to test MutableLiveData https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()
    @Test
    fun testAddDrawCommandOnceShouldReturnListofOne(){
        //PREPARE
        val repostiroy:CanvasCommandHistoryRepostiroy = getRepository()

        //PERFORM
        val drawCommand = CanvasCommand( CommandType.DRAW, Paint())
        repostiroy.addCanvasCommand(drawCommand)
        val result = repostiroy.getDrawCommandHistory()


        //CHECK
        Assert.assertEquals(result.value!!.size,1)
    }

    private fun getRepository():CanvasCommandHistoryRepostiroy{
        return CanvasCommandHistoryRepostiroy()
    }
}