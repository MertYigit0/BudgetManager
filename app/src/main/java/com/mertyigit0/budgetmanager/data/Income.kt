package com.mertyigit0.budgetmanager.data

data class Income(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val currency: String,
    val categoryId: Int,
    val categoryName: String,
    val date: String,
    val note: String?,
    val createdAt: String
)

data class User(
    val id: Int,
    val email: String,
    val createdAt: String,
    val currency: String,
    val notificationEnabled: Boolean
    )


data class Expense(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val currency: String,
    val categoryId: Int,
    val categoryName: String,
    val date: String,
    val note: String?,
    val createdAt: String
)

data class ExpenseCategory(
    val id: Int,
    val userId: Int,
    val name: String
)


data class IncomeCategory(
    val id: Int,
    val userId: Int,
    val name: String
)

data class FinancialGoal(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val targetAmount: Double,
    var currentAmount: Double,
    val deadline: String,
    val createdAt: String,
    val categoryId: Int,
    val percentage: Int
)

data class RecurringPayment(
    val id: Int,
    val userId: Int,
    val title: String,
    val amount: Double,
    val currency: String,
    val recurrence: String,
    val nextPaymentDate: String,
    val categoryId: Int
)

data class RegularIncome(
    val id: Int,
    val userId: Int,
    val title: String,
    val amount: Double,
    val currency: String,
    val recurrence: String,
    val date: String,
    val categoryId: Int
)


data class Debt(
    val id: Int,
    val userId: Int,
    val amount: Double,
    val currency: String,
    val dueDate: String,
    val isLent: Boolean,
    val personName: String?,
    val note: String?,
    val createdAt: String
)

data class FinancialSuggestion(
    val id: Int,
    val userId: Int,
    val suggestionText: String,
    val createdAt: String
)

data class Reminder(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String?,
    val reminderDate: String,
    val createdAt: String
)

data class BudgetAlert(
    val id: Int,
    val userId: Int,
    val alertType: String,
    val message: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val createdAt: String,
    val categoryId: Int?

)

