package com.mertyigit0.budgetmanager.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income

class IncomeAlarmReceiver : BroadcastReceiver() {

    /*
    override fun onReceive(context: Context, intent: Intent) {

        val regularIncomeId = intent.getIntExtra("regularIncomeId", -1)
        println("regularincomeid$regularIncomeId")
        if (regularIncomeId != -1) {

            val dbHelper = DatabaseHelper(context)
            val regularIncome = dbHelper.getRegularIncomeById(regularIncomeId)
            if (regularIncome != null) {
                // Geliri veritabanına ekle
                val isSuccess = dbHelper.addIncome(
                    Income(
                        id = 0,
                        userId = regularIncome.userId,
                        amount = regularIncome.amount,
                        currency = regularIncome.currency,
                        categoryId = regularIncome.categoryId,
                        categoryName = regularIncome.categoryName,
                        date = "a", // Geçerli tarih kullanılabilir
                        note = regularIncome.title, // Başlık olarak kullanılabilir
                        createdAt = "a" // Gerekirse oluşturulan tarihi buraya ekleyin
                    )
                )
                if (isSuccess) {

                    println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                } else {
                    println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb")
                }
            } else {

            }
        }
    }

     */
    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }

}
