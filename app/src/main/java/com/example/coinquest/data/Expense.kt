package com.example.coinquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long, // Store as timestamp
    val startTime: String,
    val endTime: String,
    val description: String,
    val amount: Double,
    val categoryId: Int,
    val photoPath: String? = null
)