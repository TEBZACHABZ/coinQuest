package com.example.coinquest

import com.example.coinquest.data.Badge
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testBadgeLogic_BudgetMaster_Earned() {
        val totalSpending = 750.0
        val minGoal = 500.0
        val maxGoal = 1000.0
        
        val isBudgetMaster = maxGoal > 0 && totalSpending <= maxGoal && totalSpending >= minGoal
        assertTrue(isBudgetMaster)
    }

    @Test
    fun testBadgeLogic_ActiveLogger_NotEarned() {
        val expensesCount = 3
        val isEarned = expensesCount >= 5
        assertFalse(isEarned)
    }

    @Test
    fun testBadgeLogic_BigSaver_Earned() {
        val totalSpending = 300.0
        val minGoal = 500.0
        val isBigSaver = minGoal > 0 && totalSpending < minGoal * 0.8
        assertTrue(isBigSaver)
    }
}
