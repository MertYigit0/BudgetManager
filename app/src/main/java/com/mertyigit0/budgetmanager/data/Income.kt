package com.mertyigit0.budgetmanager.data



data class Income(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val currency: String,
    val categoryId: Int,
    val date: String,
    val note: String?,
    val createdAt: String
)


data class Category(
    val id: Int,
    val name: String
)
