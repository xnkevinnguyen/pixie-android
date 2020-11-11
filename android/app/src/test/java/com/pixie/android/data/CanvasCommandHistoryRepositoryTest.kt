package com.pixie.android.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pixie.android.data.draw.CanvasRepository
import org.junit.Rule
import org.junit.rules.TestRule


class CanvasCommandHistoryRepositoryTest {
    // Rule used to test MutableLiveData https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()
//    @Test
//    fun testAddDrawCommandOnceShouldReturnListofOne(){
//        //PREPARE
//        val repository:CanvasCommandHistoryRepository = getRepository()
//
//        //PERFORM
//        val drawCommand = CanvasCommand( CommandType.DRAW, Paint())
//        repository.addCanvasCommand(drawCommand)
//        val result = repository.getDrawCommandHistory()
//
//
//        //CHECK
//        Assert.assertEquals(result.value!!.size,1)
//    }

    private fun getRepository():CanvasRepository{
        return CanvasRepository()
    }
}