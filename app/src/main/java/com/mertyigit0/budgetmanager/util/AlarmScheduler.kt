package com.mertyigit0.budgetmanager.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmScheduler {

    private const val REQUEST_CODE_PREFIX = 1000 // İsteğin benzersiz olması için bir önek

    const val ONCE_A_WEEK = AlarmManager.INTERVAL_FIFTEEN_MINUTES / 120
    const val ONCE_A_MONTH = AlarmManager.INTERVAL_DAY * 30 // Yaklaşık olarak 30 gün

    fun scheduleRepeatingIncome(context: Context, regularIncomeId: Int, startTime: Long, interval: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, IncomeAlarmReceiver::class.java).apply {
            putExtra("regularIncomeId", regularIncomeId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            regularIncomeId + REQUEST_CODE_PREFIX,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent)
    }

    fun cancelRepeatingIncome(context: Context, incomeId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, IncomeAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            incomeId + REQUEST_CODE_PREFIX,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
