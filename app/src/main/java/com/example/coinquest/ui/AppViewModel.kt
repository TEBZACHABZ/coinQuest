package com.example.coinquest.ui

import android.app.Application
import androidx.lifecycle.*
import com.example.coinquest.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    val allCategories: LiveData<List<Category>>
    val goal: LiveData<Goal?>

    init {
        val appDao = AppDatabase.getDatabase(application).appDao()
        repository = AppRepository(appDao)
        allCategories = repository.allCategories
        goal = repository.goal
    }

    // User operations
    suspend fun registerUser(user: User) = withContext(Dispatchers.IO) {
        repository.insertUser(user)
    }

    suspend fun loginUser(username: String): User? = withContext(Dispatchers.IO) {
        repository.getUserByUsername(username)
    }

    // Category operations
    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    // Expense operations
    fun insertExpense(expense: Expense) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertExpense(expense)
    }

    fun getExpensesBetweenDates(startDate: Long, endDate: Long): LiveData<List<Expense>> {
        return repository.getExpensesBetweenDates(startDate, endDate)
    }

    fun getCategorySpending(startDate: Long, endDate: Long): LiveData<List<CategorySpending>> {
        return repository.getCategorySpending(startDate, endDate)
    }

    fun getExpensesCount(startDate: Long, endDate: Long): LiveData<Int> {
        return repository.getExpensesBetweenDates(startDate, endDate).map { it.size }
    }

    // Goal operations
    fun setGoal(goal: Goal) = viewModelScope.launch(Dispatchers.IO) {
        repository.setGoal(goal)
    }

    fun getBadges(totalSpending: Double, expensesCount: Int, minGoal: Double, maxGoal: Double): List<Badge> {
        val badges = mutableListOf<Badge>()
        
        // Budget Master
        if (maxGoal > 0 && totalSpending <= maxGoal && totalSpending >= minGoal) {
            badges.add(Badge("Budget Master", "Stayed within your goals!", android.R.drawable.ic_menu_compass, true))
        } else {
            badges.add(Badge("Budget Master", "Stay within your goals to earn this.", android.R.drawable.ic_menu_compass, false))
        }

        // Active Logger
        if (expensesCount >= 5) {
            badges.add(Badge("Active Logger", "Logged 5 or more expenses.", android.R.drawable.ic_menu_edit, true))
        } else {
            badges.add(Badge("Active Logger", "Log 5 expenses to earn this.", android.R.drawable.ic_menu_edit, false))
        }

        // Big Saver
        if (minGoal > 0 && totalSpending < minGoal * 0.8) {
            badges.add(Badge("Big Saver", "Spent 20% less than your min goal!", android.R.drawable.ic_menu_save, true))
        } else {
            badges.add(Badge("Big Saver", "Save even more to earn this.", android.R.drawable.ic_menu_save, false))
        }

        return badges
    }
}