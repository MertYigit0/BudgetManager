package com.mertyigit0.budgetmanager.data



data class Income(
    val id: Int,
    val amount: Double,
    val category: String,
    val date: String,
    val description: String
)
