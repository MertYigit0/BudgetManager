package com.mertyigit0.budgetmanager.util

import com.mertyigit0.budgetmanager.data.DatabaseHelper
import org.apache.commons.math3.stat.regression.SimpleRegression

class LinearRegression {

/*
    fun getAllIncomesAndExpensesForMonths(userId: Int, startMonth: Int, endMonth: Int): Pair<List<Double>, List<Double>> {

     // val dbHelper = DatabaseHelper(requireContext()) // DatabaseHelper sınıfının bir örneğini oluştur
        val allIncomeData = mutableListOf<Double>()
        val allExpenseData = mutableListOf<Double>()

        for (month in startMonth..endMonth) {
            val incomes = dbHelper.getAllIncomesForMonth(userId, month)
            val expenses = dbHelper.getAllExpensesForMonth(userId, month)

            incomes.forEach { income ->
                allIncomeData.add(income.amount)
            }

            expenses.forEach { expense ->
                allExpenseData.add(expense.amount)
            }
        }

        return Pair(allIncomeData, allExpenseData)
    }

    fun predictForMonths(userId: Int, startMonth: Int, endMonth: Int) {
        val (allIncomeData, allExpenseData) = getAllIncomesAndExpensesForMonths(userId, startMonth, endMonth)

        val linearRegression = LinearRegression()

        for (month in (endMonth + 1)..(endMonth + 3)) {

            val targetDay = 1.0
            val predictedValue = linearRegression.calculatePrediction(allIncomeData, allExpenseData, targetDay)
            println("Tahminler $month. ay için: $predictedValue")
        }
    }

    fun calculatePrediction(incomeAmounts: List<Double>, expenseAmounts: List<Double>, targetDay: Double): Double {
        val regression = SimpleRegression()

        // Gelir verilerini modele ekle
        incomeAmounts.forEachIndexed { index, amount ->
            regression.addData(index.toDouble(), amount)
        }

        // Gider verilerini modele ekle
        expenseAmounts.forEachIndexed { index, amount ->
            regression.addData(index.toDouble(), -amount) // Giderler negatif olarak eklenir
        }

        return regression.predict(targetDay)
    }

 */
}
