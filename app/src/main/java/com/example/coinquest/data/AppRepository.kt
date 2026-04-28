package com.example.coinquest.data

import androidx.lifecycle.LiveData

class AppRepository(private val appDao: AppDao) {

    val allCategories: LiveData<List<Category>> = appDao.getAllCategories()
    val goal: LiveData<Goal?> = appDao.getGoal()

    suspend fun insertUser(user: User) = appDao.insertUser(user)
    suspend fun getUserByUsername(username: String) = appDao.getUserByUsername(username)

    suspend fun insertCategory(category: Category) = appDao.insertCategory(category)

    suspend fun insertExpense(expense: Expense) = appDao.insertExpense(expense)

    fun getExpensesBetweenDates(startDate: Long, endDate: Long) = 
        appDao.getExpensesBetweenDates(startDate, endDate)

    fun getCategorySpending(startDate: Long, endDate: Long) = 
        appDao.getCategorySpending(startDate, endDate)

    suspend fun setGoal(goal: Goal) = appDao.setGoal(goal)
}