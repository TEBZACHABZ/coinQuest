package com.example.coinquest

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun validateGoalLogic() {
        val minGoal = 500.0
        val maxGoal = 1000.0
        assertTrue(minGoal < maxGoal)
    }
}