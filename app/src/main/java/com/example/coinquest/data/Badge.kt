package com.example.coinquest.data

data class Badge(
    val name: String,
    val description: String,
    val iconResId: Int,
    val isEarned: Boolean = false
)