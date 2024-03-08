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

        //incomes
        private const val TABLE_INCOMES = "incomes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_NOTE = "note"

        //categories
        private const val TABLE_CATEGORIES = "categories"
        private const val COLUMN_CATEGORY_NAME = "category_name"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_INCOMES_TABLE = ("CREATE TABLE $TABLE_INCOMES(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_AMOUNT REAL NOT NULL," +
                "$COLUMN_CATEGORY_ID INTEGER," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_NOTE TEXT)")


        val CREATE_CATEGORIES_TABLE = ("CREATE TABLE $TABLE_CATEGORIES(" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${COLUMN_CATEGORY_NAME}NAME TEXT NOT NULL)")

        db?.execSQL(CREATE_INCOMES_TABLE)
        db?.execSQL(CREATE_CATEGORIES_TABLE)


        // Başlangıçta 4 kategori ekle
        insertCategory(db, "Salary")
        insertCategory(db, "Investment")
        insertCategory(db, "Rent")
        insertCategory(db, "Other")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INCOMES")
        onCreate(db)
    }

    fun addIncome(income: Income): Boolean {
        val values = ContentValues()
        values.put(COLUMN_AMOUNT, income.amount)
        values.put(COLUMN_CATEGORY_ID, income.categoryId)
        values.put(COLUMN_DATE, income.date)
        values.put(COLUMN_NOTE, income.description)

        val db = this.writableDatabase
        val success = db.insert(TABLE_INCOMES, null, values)
        db.close()
        return (Integer.parseInt("$success") != -1)
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
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_AMOUNT)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOTE))
                )
                incomesList.add(income)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return incomesList
    }

    private fun insertCategory(db: SQLiteDatabase?, categoryName: String) {
        val values = ContentValues()
        values.put(COLUMN_CATEGORY_NAME, categoryName)
        db?.insert(TABLE_CATEGORIES, null, values)
    }



}
