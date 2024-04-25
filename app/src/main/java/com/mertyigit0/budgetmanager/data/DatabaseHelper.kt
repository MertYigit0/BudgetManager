package com.mertyigit0.budgetmanager.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mertyigit0.budgetmanager.data.Income
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "budget_manager.db"
        private const val DATABASE_VERSION = 3


        //users
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_CURRENCY = "currency"
        private const val COLUMN_NOTIFICATION_ENABLED = "reminder_enabled"
        private const val COLUMN_CREATED_AT_USER = "created_at"


        //Expenses
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID_EXPENSE = "id"
        private const val COLUMN_USER_ID_EXPENSE = "user_id"
        private const val COLUMN_AMOUNT_EXPENSE = "amount"
        private const val COLUMN_CURRENCY_EXPENSE = "currency"
        private const val COLUMN_DATE_EXPENSE = "date"
        private const val COLUMN_CATEGORY_ID_EXPENSE = "category_id"
        private const val COLUMN_CATEGORY_NAME_EXPENSE = "category_name"
        private const val COLUMN_NOTE_EXPENSE = "note"
        private const val COLUMN_CREATED_AT_EXPENSE = "created_at"

        //Expense Categories
        private const val TABLE_EXPENSE_CATEGORIES = "expense_categories"
        private const val COLUMN_ID_EXPENSE_CATEGORY = "id"
        private const val COLUMN_USER_ID_EXPENSE_CATEGORY = "user_id"
        private const val COLUMN_NAME_EXPENSE_CATEGORY = "name"

        //incomes
        private const val TABLE_INCOMES = "incomes"
        private const val COLUMN_ID_INCOME = "id"
        private const val COLUMN_USER_ID_INCOME = "user_id"
        private const val COLUMN_AMOUNT_INCOME = "amount"
        private const val COLUMN_CURRENCY_INCOME = "currency"
        private const val COLUMN_DATE_INCOME = "date"
        private const val COLUMN_CATEGORY_ID_INCOME = "category_id"
        private const val COLUMN_CATEGORY_NAME_INCOME = "category_name"
        private const val COLUMN_NOTE_INCOME = "note"
        private const val COLUMN_CREATED_AT_INCOME = "created_at"

        //Income Categories
        private const val TABLE_INCOME_CATEGORIES = "income_categories"
        private const val COLUMN_ID_INCOME_CATEGORY = "id"
        private const val COLUMN_USER_ID_INCOME_CATEGORY = "user_id"
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
        private const val COLUMN_ALERT_TYPE_BUDGET_ALERT = "alert_type"
        private const val COLUMN_MESSAGE_BUDGET_ALERT = "message"
        private const val COLUMN_CREATED_AT_BUDGET_ALERT = "created_at"
        private const val COLUMN_TARGET_AMOUNT_BUDGET_ALERT = "target_amount"
        private const val COLUMN_CURRENT_AMOUNT_BUDGET_ALERT = "current_amount"
        private const val COLUMN_CATEGORY_ID_BUDGET_ALERT = "category_id"

        //Regular Incomes
        private const val TABLE_REGULAR_INCOMES = "regular_incomes"
        private const val COLUMN_ID_REGULAR_INCOME = "id"
        private const val COLUMN_USER_ID_REGULAR_INCOME = "user_id"
        private const val COLUMN_TITLE_REGULAR_INCOME = "title"
        private const val COLUMN_AMOUNT_REGULAR_INCOME = "amount"
        private const val COLUMN_CURRENCY_REGULAR_INCOME = "currency"
        private const val COLUMN_RECURRENCE_REGULAR_INCOME = "recurrence"
        private const val COLUMN_DATE_REGULAR_INCOME = "date"
        private const val COLUMN_CATEGORY_ID_REGULAR_INCOME = "category_id"


        private const val TABLE_NAME = "exchange_rates"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CURRENCY_CODE = "currency_code"
        private const val COLUMN_RATE = "rate"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_CURRENCY_CODE TEXT, $COLUMN_RATE REAL)"
        db?.execSQL(createTableQuery)


        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS(" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMAIL TEXT NOT NULL UNIQUE," +
                "$COLUMN_CURRENCY TEXT," +
                "$COLUMN_NOTIFICATION_ENABLED INTEGER," +
                "$COLUMN_CREATED_AT_USER TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
        db?.execSQL(CREATE_USERS_TABLE)


        // Income categories table creation
        val CREATE_INCOME_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_INCOME_CATEGORIES(" +
                "$COLUMN_ID_INCOME_CATEGORY INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_INCOME_CATEGORY INTEGER NOT NULL," +
                "$COLUMN_NAME_INCOME_CATEGORY TEXT NOT NULL)")
                "FOREIGN KEY($COLUMN_USER_ID_INCOME_CATEGORY) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
        db?.execSQL(CREATE_INCOME_CATEGORIES_TABLE)

        // Budget Alerts
        val CREATE_BUDGET_ALERTS_TABLE = ("CREATE TABLE $TABLE_BUDGET_ALERTS(" +
                "$COLUMN_ID_BUDGET_ALERT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_BUDGET_ALERT INTEGER NOT NULL," +
                "$COLUMN_ALERT_TYPE_BUDGET_ALERT TEXT," +
                "$COLUMN_MESSAGE_BUDGET_ALERT TEXT," +
                "$COLUMN_CREATED_AT_BUDGET_ALERT TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "$COLUMN_CATEGORY_ID_BUDGET_ALERT INTEGER," +
                "$COLUMN_TARGET_AMOUNT_BUDGET_ALERT REAL," +
                "$COLUMN_CURRENT_AMOUNT_BUDGET_ALERT REAL," +
                "FOREIGN KEY($COLUMN_USER_ID_BUDGET_ALERT) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_BUDGET_ALERT) REFERENCES $TABLE_EXPENSE_CATEGORIES($COLUMN_CATEGORY_ID_EXPENSE))")
        db?.execSQL(CREATE_BUDGET_ALERTS_TABLE)

        // Incomes table creation
        val CREATE_INCOMES_TABLE = ("CREATE TABLE $TABLE_INCOMES(" +
                "$COLUMN_ID_INCOME INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_INCOME INTEGER NOT NULL," +
                "$COLUMN_AMOUNT_INCOME REAL NOT NULL," +
                "$COLUMN_CURRENCY_INCOME TEXT NOT NULL," +
                "$COLUMN_DATE_INCOME TEXT NOT NULL," +
                "$COLUMN_CATEGORY_ID_INCOME INTEGER NOT NULL," +
                "$COLUMN_CATEGORY_NAME_INCOME TEXT NOT NULL," +
                "$COLUMN_NOTE_INCOME TEXT," +
                "$COLUMN_CREATED_AT_INCOME TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_CATEGORY_NAME_INCOME) REFERENCES $TABLE_INCOME_CATEGORIES($COLUMN_NAME_INCOME_CATEGORY)," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_INCOME) REFERENCES $TABLE_INCOME_CATEGORIES($COLUMN_ID_INCOME_CATEGORY)," +
                "FOREIGN KEY($COLUMN_USER_ID_INCOME) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_INCOMES_TABLE)

        val CREATE_FINANCIAL_SUGGESTIONS_TABLE = ("CREATE TABLE $TABLE_FINANCIAL_SUGGESTIONS(" +
                "$COLUMN_ID_FINANCIAL_SUGGESTION INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_FINANCIAL_SUGGESTION INTEGER NOT NULL," +
                "$COLUMN_SUGGESTION_TEXT TEXT," +
                "$COLUMN_CREATED_AT_FINANCIAL_SUGGESTION TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_USER_ID_FINANCIAL_SUGGESTION) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_FINANCIAL_SUGGESTIONS_TABLE)

        val CREATE_FINANCIAL_GOALS_TABLE = ("CREATE TABLE $TABLE_FINANCIAL_GOALS (" +
                "$COLUMN_ID_FINANCIAL_GOAL INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_FINANCIAL_GOAL INTEGER NOT NULL," +
                "$COLUMN_TITLE_FINANCIAL_GOAL TEXT," +
                "$COLUMN_DESCRIPTION_FINANCIAL_GOAL TEXT," +
                "$COLUMN_TARGET_AMOUNT_FINANCIAL_GOAL REAL," +
                "$COLUMN_CURRENT_AMOUNT_FINANCIAL_GOAL REAL," +
                "$COLUMN_DEADLINE_FINANCIAL_GOAL TEXT," +
                "$COLUMN_CREATED_AT_FINANCIAL_GOAL TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_USER_ID_FINANCIAL_GOAL) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_FINANCIAL_GOALS_TABLE)

        val CREATE_RECURRING_PAYMENTS_TABLE = ("CREATE TABLE $TABLE_RECURRING_PAYMENTS (" +
                "$COLUMN_ID_RECURRING_PAYMENT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_RECURRING_PAYMENT INTEGER NOT NULL," +
                "$COLUMN_TITLE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_AMOUNT_RECURRING_PAYMENT REAL," +
                "$COLUMN_CURRENCY_RECURRING_PAYMENT TEXT," +
                "$COLUMN_RECURRENCE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_NEXT_PAYMENT_DATE_RECURRING_PAYMENT TEXT," +
                "$COLUMN_CATEGORY_ID_RECURRING_PAYMENT INTEGER," +
                "FOREIGN KEY($COLUMN_USER_ID_RECURRING_PAYMENT) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_RECURRING_PAYMENTS_TABLE)

        val CREATE_DEBTS_TABLE = ("CREATE TABLE $TABLE_DEBTS (" +
                "$COLUMN_ID_DEBT INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_DEBT INTEGER NOT NULL," +
                "$COLUMN_AMOUNT_DEBT REAL," +
                "$COLUMN_CURRENCY_DEBT TEXT," +
                "$COLUMN_DUE_DATE_DEBT TEXT," +
                "$COLUMN_IS_LENT_DEBT INTEGER," +
                "$COLUMN_PERSON_NAME_DEBT TEXT," +
                "$COLUMN_NOTE_DEBT TEXT," +
                "$COLUMN_CREATED_AT_DEBT TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_USER_ID_DEBT) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_DEBTS_TABLE)

        // Regular Incomes
        val CREATE_REGULAR_INCOMES_TABLE = ("CREATE TABLE $TABLE_REGULAR_INCOMES(" +
                "$COLUMN_ID_REGULAR_INCOME INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_REGULAR_INCOME INTEGER," +
                "$COLUMN_TITLE_REGULAR_INCOME TEXT," +
                "$COLUMN_AMOUNT_REGULAR_INCOME REAL," +
                "$COLUMN_CURRENCY_REGULAR_INCOME TEXT," +
                "$COLUMN_RECURRENCE_REGULAR_INCOME TEXT," +
                "$COLUMN_DATE_REGULAR_INCOME TEXT," +
                "$COLUMN_CATEGORY_ID_REGULAR_INCOME INTEGER," +
                "FOREIGN KEY($COLUMN_USER_ID_REGULAR_INCOME) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_REGULAR_INCOME) REFERENCES $TABLE_INCOME_CATEGORIES($COLUMN_ID_INCOME_CATEGORY)" +
                ")")
        db?.execSQL(CREATE_REGULAR_INCOMES_TABLE)

        // Reminders
        val CREATE_REMINDERS_TABLE = ("CREATE TABLE $TABLE_REMINDERS(" +
                "$COLUMN_ID_REMINDER INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_REMINDER INTEGER NOT NULL," +
                "$COLUMN_TITLE_REMINDER TEXT," +
                "$COLUMN_DESCRIPTION_REMINDER TEXT," +
                "$COLUMN_REMINDER_DATE TEXT," +
                "$COLUMN_CREATED_AT_REMINDER TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY($COLUMN_USER_ID_REMINDER) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db?.execSQL(CREATE_REMINDERS_TABLE)

        // Expenses Table Creation
        val CREATE_EXPENSES_TABLE = ("CREATE TABLE $TABLE_EXPENSES(" +
                "$COLUMN_ID_EXPENSE INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID_EXPENSE INTEGER NOT NULL," +
                "$COLUMN_AMOUNT_EXPENSE REAL NOT NULL," +
                "$COLUMN_CURRENCY_EXPENSE TEXT NOT NULL," +
                "$COLUMN_DATE_EXPENSE TEXT NOT NULL," +
                "$COLUMN_CATEGORY_ID_EXPENSE INTEGER NOT NULL," +
                "$COLUMN_CATEGORY_NAME_EXPENSE TEXT NOT NULL," +
                "$COLUMN_NOTE_EXPENSE TEXT," +
                "$COLUMN_CREATED_AT_EXPENSE TEXT," +
                "FOREIGN KEY($COLUMN_CATEGORY_NAME_EXPENSE) REFERENCES $TABLE_EXPENSE_CATEGORIES($COLUMN_NAME_EXPENSE_CATEGORY)," +
                "FOREIGN KEY($COLUMN_CATEGORY_ID_EXPENSE) REFERENCES $TABLE_EXPENSE_CATEGORIES($COLUMN_ID_EXPENSE_CATEGORY)," +
                "FOREIGN KEY($COLUMN_USER_ID_EXPENSE) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")

        db?.execSQL(CREATE_EXPENSES_TABLE)

        // Expense Categories Table Creation
        val CREATE_EXPENSE_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_EXPENSE_CATEGORIES(" +
                "$COLUMN_ID_EXPENSE_CATEGORY INTEGER PRIMARY KEY," +
                "$COLUMN_USER_ID_EXPENSE_CATEGORY INTEGER NOT NULL," +
                "$COLUMN_NAME_EXPENSE_CATEGORY TEXT NOT NULL)")
        "FOREIGN KEY($COLUMN_USER_ID_EXPENSE_CATEGORY) REFERENCES $TABLE_USERS($COLUMN_USER_ID)," +

        db?.execSQL(CREATE_EXPENSE_CATEGORIES_TABLE)


        if (db != null) {
            insertDefaultIncomeCategories(db)
            insertDefaultExpenseCategories(db)
        }
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Eski tabloları sil ve yeniden oluştur
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INCOMES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSE_CATEGORIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INCOME_CATEGORIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECURRING_PAYMENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_DEBTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REGULAR_INCOMES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_REMINDERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FINANCIAL_GOALS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FINANCIAL_SUGGESTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGET_ALERTS")

        // Yeniden oluştur
        onCreate(db)
    }
    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_CREATED_AT_USER, user.createdAt)
            put(COLUMN_CURRENCY, user.currency)
            put(COLUMN_NOTIFICATION_ENABLED, if (user.notificationEnabled) 1 else 0)
        }
        val success = try {
            db.insertOrThrow(TABLE_USERS, null, values)
        } catch (e: SQLException) {
            // Hata durumunda burada işlem yapılabilir
            // Örneğin: Loglama veya hata mesajı gösterme
            e.printStackTrace()
            -1
        } finally {
            db.close()
        }
        return success != -1L
    }

    fun addIncome(income: Income): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT_INCOME, income.amount)
            put(COLUMN_USER_ID_INCOME, income.userId) // Kullanıcı kimliği eklendi
            put(COLUMN_CURRENCY_INCOME, income.currency) // Para birimi eklendi
            put(COLUMN_DATE_INCOME, income.date)
            put(COLUMN_CATEGORY_ID_INCOME, income.categoryId)
            put(COLUMN_CATEGORY_NAME_INCOME, income.categoryName)
            put(COLUMN_NOTE_INCOME, income.note)
            put(COLUMN_CREATED_AT_INCOME, income.createdAt) // Oluşturulma tarihi eklendi
        }

        val db = this.writableDatabase
        val success = db.insert(TABLE_INCOMES, null, values)
        db.close()
        return success != -1L
    }

    fun deleteIncome(incomeId: Long): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_INCOMES, "$COLUMN_ID_INCOME=?", arrayOf(incomeId.toString()))
        db.close()
        return success > 0
    }


    @SuppressLint("Range")
    fun getAllIncomesByUserId(userId: Int): List<Income> {
        val incomesList = ArrayList<Income>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_INCOMES WHERE $COLUMN_USER_ID_INCOME = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val income = Income(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID_INCOME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID_INCOME)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY_INCOME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_INCOME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME_INCOME)),
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
    @SuppressLint("Range")
    fun getAllExpensesByUserId(userId: Int): List<Expense> {
        val expensesList = ArrayList<Expense>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_USER_ID_EXPENSE = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val expense = Expense(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID_EXPENSE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID_EXPENSE)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT_EXPENSE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY_EXPENSE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_EXPENSE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME_EXPENSE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_EXPENSE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE_EXPENSE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT_EXPENSE))
                )
                expensesList.add(expense)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return expensesList
    }
    fun addExpense(expense: Expense): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT_EXPENSE, expense.amount)
            put(COLUMN_USER_ID_EXPENSE, expense.userId)
            put(COLUMN_CURRENCY_EXPENSE, expense.currency)
            put(COLUMN_CATEGORY_ID_EXPENSE, expense.categoryId)
            put(COLUMN_CATEGORY_NAME_EXPENSE, expense.categoryName)
            put(COLUMN_DATE_EXPENSE, expense.date)
            put(COLUMN_NOTE_EXPENSE, expense.note)
            put(COLUMN_CREATED_AT_EXPENSE, expense.createdAt)
        }

        val db = this.writableDatabase
        val success = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return success != -1L
    }

    fun deleteExpense(expenseId: Long): Boolean {
        val db = this.writableDatabase
        val success = db.delete(TABLE_EXPENSES, "$COLUMN_ID_EXPENSE=?", arrayOf(expenseId.toString()))
        db.close()
        return success > 0
    }


    @SuppressLint("Range")
    fun getUserData(email: String): User? {
        var user: User? = null
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(email))

        cursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
                val userEmail = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                val createdAt = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT_USER))
                val currency = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENCY))
                val notificationEnabledInt = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATION_ENABLED))
                val notificationEnabled = notificationEnabledInt != 0
                user = User(id, userEmail, createdAt,currency,notificationEnabled)
            }
        }

        cursor.close()
        db.close()

        return user
    }



    fun addFinancialGoal(goal: FinancialGoal): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_FINANCIAL_GOAL, goal.userId)
            put(COLUMN_TITLE_FINANCIAL_GOAL, goal.title)
            put(COLUMN_DESCRIPTION_FINANCIAL_GOAL, goal.description)
            put(COLUMN_TARGET_AMOUNT_FINANCIAL_GOAL, goal.targetAmount)
            put(COLUMN_CURRENT_AMOUNT_FINANCIAL_GOAL, goal.currentAmount)
            put(COLUMN_DEADLINE_FINANCIAL_GOAL, goal.deadline)
            // Diğer sütunlar default değerleri alacakları için eklemeye gerek yok
        }

        val db = this.writableDatabase
        val success = db.insert(TABLE_FINANCIAL_GOALS, null, values)
        db.close()
        return success != -1L
    }
    fun deleteFinancialGoal(goalId: Int): Boolean {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID_FINANCIAL_GOAL = ?"
        val whereArgs = arrayOf(goalId.toString())
        val success = db.delete(TABLE_FINANCIAL_GOALS, whereClause, whereArgs) > 0
        db.close()
        return success
    }





    @SuppressLint("Range")
    fun getAllFinancialGoalsByUserId(userId: Int): List<FinancialGoal> {
        val financialGoalsList = mutableListOf<FinancialGoal>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_FINANCIAL_GOALS WHERE $COLUMN_USER_ID_FINANCIAL_GOAL = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val financialGoal = FinancialGoal(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID_FINANCIAL_GOAL)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID_FINANCIAL_GOAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE_FINANCIAL_GOAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION_FINANCIAL_GOAL)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_TARGET_AMOUNT_FINANCIAL_GOAL)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_CURRENT_AMOUNT_FINANCIAL_GOAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DEADLINE_FINANCIAL_GOAL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT_FINANCIAL_GOAL))
                )
                financialGoalsList.add(financialGoal)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return financialGoalsList
    }

    fun addBudgetAlert(budgetAlert: BudgetAlert): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_BUDGET_ALERT, budgetAlert.userId)
            put(COLUMN_ALERT_TYPE_BUDGET_ALERT, budgetAlert.alertType)
            put(COLUMN_MESSAGE_BUDGET_ALERT, budgetAlert.message)
            put(COLUMN_CATEGORY_ID_BUDGET_ALERT, budgetAlert.categoryId) // categoryId kullanıldı
            put(COLUMN_TARGET_AMOUNT_BUDGET_ALERT, budgetAlert.targetAmount)
            put(COLUMN_CURRENT_AMOUNT_BUDGET_ALERT, budgetAlert.currentAmount)
        }
        val success = db.insert(TABLE_BUDGET_ALERTS, null, values)
        db.close()
        return success != -1L
    }

    fun deleteBudgetAlert(budgetAlertId: Int): Boolean {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID_BUDGET_ALERT = ?"
        val whereArgs = arrayOf(budgetAlertId.toString())
        val success = db.delete(TABLE_BUDGET_ALERTS, whereClause, whereArgs) > 0
        db.close()
        return success
    }


    @SuppressLint("Range")
    fun getAllBudgetAlertsByUserId(userId: Int): List<BudgetAlert> {
        val budgetAlerts = ArrayList<BudgetAlert>()
        val selectQuery = "SELECT * FROM $TABLE_BUDGET_ALERTS WHERE $COLUMN_USER_ID_BUDGET_ALERT = $userId"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_BUDGET_ALERT))
                    val userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID_BUDGET_ALERT))
                    val alertType = cursor.getString(cursor.getColumnIndex(COLUMN_ALERT_TYPE_BUDGET_ALERT))
                    val message = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_BUDGET_ALERT))
                    val categoryId = cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID_BUDGET_ALERT))
                    val targetAmount = cursor.getDouble(cursor.getColumnIndex(COLUMN_TARGET_AMOUNT_BUDGET_ALERT))
                    val currentAmount = cursor.getDouble(cursor.getColumnIndex(COLUMN_CURRENT_AMOUNT_BUDGET_ALERT))
                    val createdAt = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_AT_BUDGET_ALERT))
                    val budgetAlert = BudgetAlert(id, userId, alertType, message,targetAmount, currentAmount ,createdAt, categoryId )
                    budgetAlerts.add(budgetAlert)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return budgetAlerts
    }






    fun insertDefaultIncomeCategories(db: SQLiteDatabase) {
        val defaultCategories = listOf("Salary", "Freelance", "Investments", "Rental Income", "Interest")
        val userId = 0

        for (category in defaultCategories) {
            val values = ContentValues().apply {
                put(COLUMN_USER_ID_INCOME_CATEGORY, userId) // Kullanıcı kimliği eklendi
                put(COLUMN_NAME_INCOME_CATEGORY, category)
            }
            db.insert(TABLE_INCOME_CATEGORIES, null, values)
        }
    }


    @SuppressLint("Range")
    fun getAllIncomeCategoriesByUserId(userId: Int): List<String> {
        val categories = mutableListOf<String>()
        val selectQuery = "SELECT * FROM $TABLE_INCOME_CATEGORIES WHERE $COLUMN_USER_ID_INCOME_CATEGORY = $userId OR $COLUMN_USER_ID_INCOME_CATEGORY = 0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val category = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_INCOME_CATEGORY))
                    categories.add(category)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return categories
    }
    @SuppressLint("Range")
    fun getIncomeCategoryIdByCategoryName(categoryName: String): Int {
        val db = this.readableDatabase
        var categoryId = -1
        val selectQuery = "SELECT $COLUMN_ID_INCOME_CATEGORY FROM $TABLE_INCOME_CATEGORIES WHERE $COLUMN_NAME_INCOME_CATEGORY = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(categoryName))
        cursor.use {
            if (it.moveToFirst()) {
                categoryId = it.getInt(it.getColumnIndex(COLUMN_ID_INCOME_CATEGORY))
            }
        }
        cursor.close()
        return categoryId
    }


    fun insertDefaultExpenseCategories(db: SQLiteDatabase) {
        val defaultCategories = listOf("Rent", "Utilities", "Food", "Transportation", "Entertainment")
        val userId = 0

        for (category in defaultCategories) {
            val values = ContentValues().apply {
                put(COLUMN_USER_ID_EXPENSE_CATEGORY, userId) // Kullanıcı kimliği eklendi
                put(COLUMN_NAME_EXPENSE_CATEGORY, category)
            }
            db.insert(TABLE_EXPENSE_CATEGORIES, null, values)
        }
    }

    @SuppressLint("Range")
    fun getAllExpenseCategoriesByUserId(userId: Int): List<String> {
        val categories = mutableListOf<String>()
        val selectQuery = "SELECT * FROM $TABLE_EXPENSE_CATEGORIES WHERE $COLUMN_USER_ID_EXPENSE_CATEGORY = $userId OR $COLUMN_USER_ID_EXPENSE_CATEGORY = 0"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        cursor.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val category = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EXPENSE_CATEGORY))
                    categories.add(category)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        return categories
    }

    @SuppressLint("Range")
    fun getExpenseCategoryIdByCategoryName(categoryName: String): Int {
        val db = this.readableDatabase
        var categoryId = -1
        val selectQuery = "SELECT $COLUMN_ID_EXPENSE_CATEGORY FROM $TABLE_EXPENSE_CATEGORIES WHERE $COLUMN_NAME_EXPENSE_CATEGORY = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(categoryName))
        cursor.use {
            if (it.moveToFirst()) {
                categoryId = it.getInt(it.getColumnIndex(COLUMN_ID_EXPENSE_CATEGORY))
            }
        }
        cursor.close()
        return categoryId
    }

    @SuppressLint("Range")
    fun getExpenseCategoryNameByCategoryId(categoryId: Int): String {
        val db = this.readableDatabase
        var categoryName = ""
        val selectQuery = "SELECT $COLUMN_NAME_EXPENSE_CATEGORY FROM $TABLE_EXPENSE_CATEGORIES WHERE $COLUMN_ID_EXPENSE_CATEGORY = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(categoryId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                categoryName = it.getString(it.getColumnIndex(COLUMN_NAME_EXPENSE_CATEGORY))
            }
        }
        cursor.close()
        return categoryName
    }



    fun getTotalExpenseForCategoryInCurrentMonth(userId: Int, categoryId: Int): Double {
        val db = this.readableDatabase
        val currentDate = getCurrentDate() // Bu ayın ilk günü ve son günü alınır
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT_EXPENSE) FROM $TABLE_EXPENSES WHERE $COLUMN_USER_ID_EXPENSE = ? AND $COLUMN_CATEGORY_ID_EXPENSE = ? AND $COLUMN_DATE_EXPENSE BETWEEN ? AND ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString(), categoryId.toString(), currentDate.first, currentDate.second))
        var totalExpense = 0.0
        cursor.use {
            if (it.moveToFirst()) {
                totalExpense = it.getDouble(0)
            }
        }
        cursor.close()
        return totalExpense
    }

    @SuppressLint("Range")
    fun getBudgetAlertForCategoryByUserId(userId: Int, categoryId: Int): BudgetAlert? {
        val db = this.readableDatabase
        var budgetAlert: BudgetAlert? = null
        val selectQuery = "SELECT * FROM $TABLE_BUDGET_ALERTS WHERE $COLUMN_USER_ID_BUDGET_ALERT = ? AND $COLUMN_CATEGORY_ID_BUDGET_ALERT = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString(), categoryId.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex(COLUMN_ID_BUDGET_ALERT))
                val alertType = it.getString(it.getColumnIndex(COLUMN_ALERT_TYPE_BUDGET_ALERT))
                val message = it.getString(it.getColumnIndex(COLUMN_MESSAGE_BUDGET_ALERT))
                val targetAmount = it.getDouble(it.getColumnIndex(COLUMN_TARGET_AMOUNT_BUDGET_ALERT))
                val currentAmount = it.getDouble(it.getColumnIndex(COLUMN_CURRENT_AMOUNT_BUDGET_ALERT))
                val createdAt = it.getString(it.getColumnIndex(COLUMN_CREATED_AT_BUDGET_ALERT))
                budgetAlert = BudgetAlert(id, userId, alertType, message, targetAmount, currentAmount, createdAt, categoryId)
            }
        }
        cursor.close()
        return budgetAlert
    }
    fun updateBudgetAlert(budgetAlert: BudgetAlert): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALERT_TYPE_BUDGET_ALERT, budgetAlert.alertType)
            put(COLUMN_MESSAGE_BUDGET_ALERT, budgetAlert.message)
            put(COLUMN_TARGET_AMOUNT_BUDGET_ALERT, budgetAlert.targetAmount)
            put(COLUMN_CURRENT_AMOUNT_BUDGET_ALERT, budgetAlert.currentAmount)
            put(COLUMN_CREATED_AT_BUDGET_ALERT, budgetAlert.createdAt)
        }
        val whereClause = "$COLUMN_ID_BUDGET_ALERT = ?"
        val whereArgs = arrayOf(budgetAlert.id.toString())
        val affectedRows = db.update(TABLE_BUDGET_ALERTS, values, whereClause, whereArgs)
        return affectedRows > 0
    }



    private fun getCurrentDate(): Pair<String, String> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val startDate = sdf.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = sdf.format(calendar.time)

        return Pair(startDate, endDate)
    }

    fun addRegularIncome(regularIncome: RegularIncome): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_REGULAR_INCOME, regularIncome.userId)
            put(COLUMN_TITLE_REGULAR_INCOME, regularIncome.title)
            put(COLUMN_AMOUNT_REGULAR_INCOME, regularIncome.amount)
            put(COLUMN_CURRENCY_REGULAR_INCOME, regularIncome.currency)
            put(COLUMN_RECURRENCE_REGULAR_INCOME, regularIncome.recurrence)
            put(COLUMN_DATE_REGULAR_INCOME, regularIncome.date)
            put(COLUMN_CATEGORY_ID_REGULAR_INCOME, regularIncome.categoryId)
        }

        val db = this.writableDatabase

        val newRowId = db.insert(TABLE_REGULAR_INCOMES, null, values)
        db.close()

        // Ekleme işlemi başarılı ise newRowId -1'den farklıdır
        return newRowId != -1L
    }


    // Kur verilerini veritabanına ekleyen işlev
    fun addExchangeRate(currencyCode: String, rate: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENCY_CODE, currencyCode)
            put(COLUMN_RATE, rate)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Veritabanından tüm kur verilerini okuyan işlev
    @SuppressLint("Range")
    fun getAllExchangeRates(): Map<String, Double> {
        val exchangeRates = mutableMapOf<String, Double>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor.use {
            while (it.moveToNext()) {
                val currencyCode = it.getString(it.getColumnIndex(COLUMN_CURRENCY_CODE))
                val rate = it.getDouble(it.getColumnIndex(COLUMN_RATE))
                exchangeRates[currencyCode] = rate
            }
        }
        db.close()
        return exchangeRates
    }

    @SuppressLint("Range")
    fun getExchangeRate(currencyCode: String): Double {
        val db = this.readableDatabase
        var exchangeRate = 0.0

        val query = "SELECT $COLUMN_RATE FROM $TABLE_NAME WHERE $COLUMN_CURRENCY_CODE = ?"
        val cursor = db.rawQuery(query, arrayOf(currencyCode))

        if (cursor.moveToFirst()) {
            exchangeRate = cursor.getDouble(cursor.getColumnIndex(COLUMN_RATE))
        }

        cursor.close()
        db.close()

        return exchangeRate
    }


    fun addExpenseCategory(userId: Int, categoryName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID_EXPENSE_CATEGORY, userId)
        values.put(COLUMN_NAME_EXPENSE_CATEGORY, categoryName)
        val id = db.insert(TABLE_EXPENSE_CATEGORIES, null, values)
        db.close()
        return id
    }


}



