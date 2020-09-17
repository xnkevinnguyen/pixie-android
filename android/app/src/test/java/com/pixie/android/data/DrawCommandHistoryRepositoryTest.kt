package com.pixie.android.data

import android.graphics.Paint
import android.graphics.Path
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pixie.android.data.draw.DrawCommandHistoryRepository
import com.pixie.android.model.draw.DrawCommand
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule


class DrawCommandHistoryRepositoryTest {
    // Rule used to test MutableLiveData https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()
    @Test
    fun testAddDrawCommandOnceShouldReturnListofOne(){
        //PREPARE
        val repository:DrawCommandHistoryRepository = getRepository()

        //PERFORM
        val drawCommand = DrawCommand( Path(), Paint())
        repository.addDrawCommand(drawCommand)
        val result = repository.getDrawCommandHistory()


        //CHECK
        Assert.assertEquals(result.value!!.size,1)
    }

    private fun getRepository():DrawCommandHistoryRepository{
        return DrawCommandHistoryRepository()
    }
}