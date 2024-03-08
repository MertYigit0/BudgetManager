package com.mertyigit0.budgetmanager.data



data class Income(
    val id: Int,
    val amount: Double,
    val categoryId: Int,
    val date: String,
    val description: String?
)

data class Category(
    val id: Int,
    val name: String
)
