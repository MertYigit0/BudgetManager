package com.mertyigit0.budgetmanager.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mertyigit0.budgetmanager.data.Income

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "budget_manager.db"
        private const val DATABASE_VERSION = 1


        //users
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_CREATED_AT_USER = "created_at"

        //Account Settings
        private const val TABLE_ACCOUNT_SETTINGS = "account_settings"
        private const val COLUMN_USER_ID_ACCOUNT = "user_id"
        private const val COLUMN_CURRENCY = "currency"
        private const val COLUMN_REMINDER_ENABLED = "reminder_enabled"
        private const val COLUMN_NOTIFICATION_ENABLED = "notification_enabled"

        //Expenses
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID_EXPENSES = "id"
        private const val COLUMN_USER_ID_EXPENSE = "user_id"
        private const val COLUMN_AMOUNT_EXPENSE = "amount"
        private const val COLUMN_CURRENCY_EXPENSE = "currency"
        private const val COLUMN_DATE_EXPENSE = "date"
        private const val COLUMN_CATEGORY_ID_EXPENSE = "category_id"
        private const val COLUMN_NOTE_EXPENSE = "note"
        private const val COLUMN_CREATED_AT_EXPENSE = "created_at"

        //Expense Categories
        private const val TABLE_EXPENSE_CATEGORIES = "expense_categories"
        private const val COLUMN_ID_EXPENSE_CATEGORY = "id"
        private const val COLUMN_NAME_EXPENSE_CATEGORY = "name"

        //incomes
        private const val TABLE_INCOMES = "incomes"
        private const val COLUMN_ID_INCOME = "id"
        private const val COLUMN_USER_ID_INCOME = "user_id"
        private const val COLUMN_AMOUNT_INCOME = "amount"
        private const val COLUMN_CURRENCY_INCOME = "currency"
        private const val COLUMN_DATE_INCOME = "date"
        private const val COLUMN_CATEGORY_ID_INCOME = "category_id"
        private const val COLUMN_NOTE_INCOME = "note"
        private const val COLUMN_CREATED_AT_INCOME = "created_at"

        //Income Categories
        private const val TABLE_INCOME_CATEGORIES = "income_categories"
        private const val COLUMN_ID_INCOME_CATEGORY = "id"
        private const val COLUMN_NAME_INCOME_CATEGORY = "name"


        // Financial Goals
        private const val TABLE_FINANCIAL_GOALS = "financial_goals"
        private const val COLUMN_ID_FINANCIAL_GOAL = "id"
        private const val COLUMN_USER_ID_FINANCIAL_GOAL = "user_id"
        private const val COLUMN_TITLE_FINANCIAL_GOAL = "title"
        private const val COLUMN_DESCRIPTION_FINANCIAL_GOAL = "description"
        private const val COLUMN_TARGET_AMOUNT_FINANCIAL_GOAL = "target_amount"
        private const val COLUMN_CURRENT_AMOUNT_FINANCIAL_GOAL = "current_amount"
        private const val COLUMN_DEADLINE_FINANCIAL_GOAL = "deadline"
        private const val COLUMN_CREATED_AT_FINANCIAL_GOAL = "created_at"

        //Recurring Payments
        private const val TABLE_RECURRING_PAYMENTS = "recurring_payments"
        private const val COLUMN_ID_RECURRING_PAYMENT = "id"
        private const val COLUMN_USER_ID_RECURRING_PAYMENT = "user_id"
        private const val COLUMN_TITLE_RECURRING_PAYMENT = "title"
        private const val COLUMN_AMOUNT_RECURRING_PAYMENT = "amount"
        private const val COLUMN_CURRENCY_RECURRING_PAYMENT = "currency"
        private const val COLUMN_RECURRENCE_RECURRING_PAYMENT = "recurrence"
        private const val COLUMN_NEXT_PAYMENT_DATE_RECURRING_PAYMENT = "next_payment_date"
        private const val COLUMN_CATEGORY_ID_RECURRING_PAYMENT = "category_id"

        //Debts
        private const val TABLE_DEBTS = "debts"
        private const val COLUMN_ID_DEBT = "id"
        private const val COLUMN_USER_ID_DEBT = "user_id"
        private const val COLUMN_AMOUNT_DEBT = "amount"
        private const val COLUMN_CURRENCY_DEBT = "currency"
        private const val COLUMN_DUE_DATE_DEBT = "due_date"
        private const val COLUMN_IS_LENT_DEBT = "is_lent"
        private const val COLUMN_PERSON_NAME_DEBT = "person_name"
        private const val COLUMN_NOTE_DEBT = "note"
        private const val COLUMN_CREATED_AT_DEBT = "created_at"

        //Financial Suggestions
        private const val TABLE_FINANCIAL_SUGGESTIONS = "financial_suggestions"
        private const val COLUMN_ID_FINANCIAL_SUGGESTION = "id"
        private const val COLUMN_USER_ID_FINANCIAL_SUGGESTION = "user_id"
        private const val COLUMN_SUGGESTION_TEXT = "suggestion_text"
        private const val COLUMN_CREATED_AT_FINANCIAL_SUGGESTION = "created_at"

        //Reminders
        private const val TABLE_REMINDERS = "reminders"
        private const val COLUMN_ID_REMINDER = "id"
        private const val COLUMN_USER_ID_REMINDER = "user_id"
        private const val COLUMN_TITLE_REMINDER = "title"
        private const val COLUMN_DESCRIPTION_REMINDER = "description"
        private const val COLUMN_REMINDER_DATE = "reminder_date"
        private const val COLUMN_CREATED_AT_REMINDER = "created_at"


        //Budget Alerts
        private const val TABLE_BUDGET_ALERTS = "budget_alerts"
        private const val COLUMN_ID_BUDGET_ALERT = "id"
        private const val COLUMN_USER_ID_BUDGET_ALERT = "user_id"
        private const val COLUMN_ALERT_TYPE = "alert_type"
        private const val COLUMN_MESSAGE = "message"
        private const val COLUMN_CREATED_AT_BUDGET_ALERT = "created_at"
        private const val COLUMN_CATEGORY_ID_BUDGET_ALERT = "category_id"





    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS(" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMAIL TEXT NOT NULL UNIQUE," +
                "$COLUMN_CREATED_AT_USER TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        db?.execSQL(CREATE_USERS_TABLE)

        // Income categories table creation
        val CREATE_INCOME_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_INCOME_CATEGORIES(" +
                "$COLUMN_ID_INCOME_CATEGORY INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME_INCOME_CATEGORY TEXT NOT NULL)")
        db?.execSQL(CREATE_INCOME_CATEGORIES_TABLE)

        // Incomes table creation
        val CREATE_INCOMES_TABLE = ("CREATE TABLE $TABLE_INCOMES(" +
                "$COLUMN_ID_INCOME INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_INCOME INTEGER NOT NULL," +
                "$COLUMN_AMOUNT_INCOME REAL NOT NULL," +
                "$COLUMN_CURRENCY_INCOME TEXT NOT NULL," +
                "$COLUMN_DATE_INCOME TEXT NOT NULL," +
                "$COLUMN_CATEGORY_ID_INCOME INTEGER NOT NULL," +
                "$COLUMN_NOTE_INCOME TEXT," +
                "$COLUMN_CREATED_AT_INCOME TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_INCOME) REFERENCES $TABLE_INCOME_CATEGORIES($COLUMN_ID_INCOME)," +
                "FOREIGN KEY($COLUMN_USER_ID_INCOME) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
                db?.execSQL(CREATE_INCOMES_TABLE)


        val CREATE_FINANCIAL_SUGGESTIONS_TABLE = ("CREATE TABLE $TABLE_FINANCIAL_SUGGESTIONS(" +
                "$COLUMN_ID_FINANCIAL_SUGGESTION INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_FINANCIAL_SUGGESTION INTEGER," +
                "$COLUMN_SUGGESTION_TEXT TEXT," +
                "$COLUMN_CREATED_AT_FINANCIAL_SUGGESTION TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
                db?.execSQL(CREATE_FINANCIAL_SUGGESTIONS_TABLE)

        val CREATE_REMINDERS_TABLE = ("CREATE TABLE $TABLE_REMINDERS(" +
                "$COLUMN_ID_REMINDER INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_REMINDER INTEGER," +
                "$COLUMN_TITLE_REMINDER TEXT," +
                "$COLUMN_DESCRIPTION_REMINDER TEXT," +
                "$COLUMN_REMINDER_DATE TEXT," +
                "$COLUMN_CREATED_AT_REMINDER TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
                db?.execSQL(CREATE_REMINDERS_TABLE)

        val CREATE_BUDGET_ALERTS_TABLE = ("CREATE TABLE $TABLE_BUDGET_ALERTS(" +
                "$COLUMN_ID_BUDGET_ALERT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_BUDGET_ALERT INTEGER," +
                "$COLUMN_ALERT_TYPE TEXT," +
                "$COLUMN_MESSAGE TEXT," +
                "$COLUMN_CREATED_AT_BUDGET_ALERT TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "$COLUMN_CATEGORY_ID_BUDGET_ALERT INTEGER)")
                 db?.execSQL(CREATE_BUDGET_ALERTS_TABLE)


        val CREATE_FINANCIAL_GOALS_TABLE = ("CREATE TABLE $TABLE_FINANCIAL_GOALS (" +
                "$COLUMN_ID_FINANCIAL_GOAL INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_FINANCIAL_GOAL INTEGER," +
                "$COLUMN_TITLE_FINANCIAL_GOAL TEXT," +
                "$COLUMN_DESCRIPTION_FINANCIAL_GOAL TEXT," +
                "$COLUMN_TARGET_AMOUNT_FINANCIAL_GOAL REAL," +
                "$COLUMN_CURRENT_AMOUNT_FINANCIAL_GOAL REAL," +
                "$COLUMN_DEADLINE_FINANCIAL_GOAL TEXT," +
                "$COLUMN_CREATED_AT_FINANCIAL_GOAL TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        db?.execSQL(CREATE_FINANCIAL_GOALS_TABLE)

        val CREATE_RECURRING_PAYMENTS_TABLE = ("CREATE TABLE $TABLE_RECURRING_PAYMENTS (" +
                "$COLUMN_ID_RECURRING_PAYMENT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_RECURRING_PAYMENT INTEGER," +
                "$COLUMN_TITLE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_AMOUNT_RECURRING_PAYMENT REAL," +
                "$COLUMN_CURRENCY_RECURRING_PAYMENT TEXT," +
                "$COLUMN_RECURRENCE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_NEXT_PAYMENT_DATE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_CATEGORY_ID_RECURRING_PAYMENT INTEGER)")
        db?.execSQL(CREATE_RECURRING_PAYMENTS_TABLE)

        val CREATE_DEBTS_TABLE = ("CREATE TABLE $TABLE_DEBTS (" +
                "$COLUMN_ID_DEBT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_DEBT INTEGER," +
                "$COLUMN_AMOUNT_DEBT REAL," +
                "$COLUMN_CURRENCY_DEBT TEXT," +
                "$COLUMN_DUE_DATE_DEBT TEXT," +
                "$COLUMN_IS_LENT_DEBT INTEGER," +
                "$COLUMN_PERSON_NAME_DEBT TEXT," +
                "$COLUMN_NOTE_DEBT TEXT," +
                "$COLUMN_CREATED_AT_DEBT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        db?.execSQL(CREATE_DEBTS_TABLE)


// Account Settings Table Creation
        val CREATE_ACCOUNT_SETTINGS_TABLE = ("CREATE TABLE $TABLE_ACCOUNT_SETTINGS(" +
                "$COLUMN_USER_ID_ACCOUNT INTEGER PRIMARY KEY," +
                "$COLUMN_CURRENCY TEXT," +
                "$COLUMN_REMINDER_ENABLED INTEGER," +
                "$COLUMN_NOTIFICATION_ENABLED INTEGER)")

        db?.execSQL(CREATE_ACCOUNT_SETTINGS_TABLE)

// Expenses Table Creation
        val CREATE_EXPENSES_TABLE = ("CREATE TABLE $TABLE_EXPENSES(" +
                "$COLUMN_ID_EXPENSES INTEGER PRIMARY KEY," +
                "$COLUMN_USER_ID_EXPENSE INTEGER," +
                "$COLUMN_AMOUNT_EXPENSE REAL," +
                "$COLUMN_CURRENCY_EXPENSE TEXT," +
                "$COLUMN_DATE_EXPENSE TEXT," +
                "$COLUMN_CATEGORY_ID_EXPENSE INTEGER," +
                "$COLUMN_NOTE_EXPENSE TEXT," +
                "$COLUMN_CREATED_AT_EXPENSE TEXT," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_EXPENSE) REFERENCES $TABLE_EXPENSE_CATEGORIES($COLUMN_ID_EXPENSE_CATEGORY)," +
                "FOREIGN KEY($COLUMN_USER_ID_EXPENSE) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")

        db?.execSQL(CREATE_EXPENSES_TABLE)

// Expense Categories Table Creation
        val CREATE_EXPENSE_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_EXPENSE_CATEGORIES(" +
                "$COLUMN_ID_EXPENSE_CATEGORY INTEGER PRIMARY KEY," +
                "$COLUMN_NAME_EXPENSE_CATEGORY TEXT)")

        db?.execSQL(CREATE_EXPENSE_CATEGORIES_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INCOMES")
        onCreate(db)
    }

    fun addIncome(income: Income): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT_INCOME, income.amount)
            put(COLUMN_USER_ID_INCOME, income.userId) // Kullanıcı kimliği eklendi
            put(COLUMN_CURRENCY_INCOME, income.currency) // Para birimi eklendi
            put(COLUMN_DATE_INCOME, income.date)
            put(COLUMN_CATEGORY_ID_INCOME, income.categoryId)
            put(COLUMN_NOTE_INCOME, income.note)
            put(COLUMN_CREATED_AT_INCOME, income.createdAt) // Oluşturulma tarihi eklendi
        }

        val db = this.writableDatabase
        val success = db.insert(TABLE_INCOMES, null, values)
        db.close()
        return success != -1L
    }


    @SuppressLint("Range")
    fun getAllIncomes(): List<Income> {
        val incomesList = ArrayList<Income>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_INCOMES"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val income = Income(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID_INCOME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY_INCOME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT_INCOME)) // Oluşturulma tarihi eklendi
                )
                incomesList.add(income)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return incomesList
    }








}
