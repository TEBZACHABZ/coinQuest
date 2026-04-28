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

    // Goal operations
    fun setGoal(goal: Goal) = viewModelScope.launch(Dispatchers.IO) {
        repository.setGoal(goal)
    }
}