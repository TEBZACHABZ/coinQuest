package com.example.coinquest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppDao {
    // User
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Category
    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    // Expense
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<Expense>>

    @Query("SELECT c.name as categoryName, SUM(e.amount) as totalAmount " +
           "FROM expenses e JOIN categories c ON e.categoryId = c.id " +
           "WHERE e.date BETWEEN :startDate AND :endDate " +
           "GROUP BY e.categoryId")
    fun getCategorySpending(startDate: Long, endDate: Long): LiveData<List<CategorySpending>>

    // Goal
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE id = 1")
    fun getGoal(): LiveData<Goal?>
}

data class CategorySpending(
    val categoryName: String,
    val totalAmount: Double
)