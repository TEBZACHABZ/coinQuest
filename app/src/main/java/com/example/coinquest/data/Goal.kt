package com.example.coinquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey val id: Int = 1, // We only need one record for the current month/settings
    val minGoal: Double,
    val maxGoal: Double
)