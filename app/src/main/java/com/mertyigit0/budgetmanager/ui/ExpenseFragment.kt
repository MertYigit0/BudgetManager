package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.ExpenseAdapter
import com.mertyigit0.budgetmanager.adapters.ExpenseSwipeToDeleteCallback
import com.mertyigit0.budgetmanager.adapters.IncomeSwipeToDeleteCallback

import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ExpenseFragment : Fragment() {

    private var _binding: FragmentExpenseBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth
    private lateinit var expenseAdapter: ExpenseAdapter

    private var currentYear: Int = 0 // Mevcut yılı saklamak için değişken
    private var currentMonth: Int = 0 // Mevcut ayı saklamak için değişken
    private var currentWeek: Int = 0 // Mevcut haftanın numarasını saklamak için değişken
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExpenseBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        expenseAdapter = ExpenseAdapter(
            requireContext(),
            ArrayList()
        ) // Boş bir ArrayList ile ExpenseAdapter oluştur

        val recyclerView = binding.expenseRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

        currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Mevcut ayı al (1 ile başlar, 0-based değil)
        currentYear = Calendar.getInstance().get(Calendar.YEAR) // Mevcut yılı al



        val itemTouchHelper = ItemTouchHelper(ExpenseSwipeToDeleteCallback(expenseAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
        expenses?.let { expenseAdapter.updateExpenseList(it) }
        // Gelir verileri listesini oluştur

        val navController = Navigation.findNavController(requireView())
        binding.toggleButtonGroup.check(R.id.expensesButton)
        binding.incomesButton.setOnClickListener {
            navController.navigate(R.id.action_expenseFragment_to_incomeFragment)
        }

        binding.addExpensebutton.setOnClickListener {
            navController.navigate(R.id.action_expenseFragment_to_addExpenseFragment)
        }



        chart_select()

        binding.buttonPreviousWeek.setOnClickListener {
            // Önceki hafta butonuna tıklandığında yapılacak işlemler
            if (binding.barTypeSpinner.selectedItemPosition == 0) {
                // Haftalık görünümdeyiz, haftayı bir önceki haftaya kaydır
                currentWeek--
                calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)
                updateWeekDatesText()
            } else {
                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth--
                if (currentMonth < 1) {
                    currentMonth = 12 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear--
                }

                calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                updateMonthYearText()
            }
        }

        binding.nextWeekButton.setOnClickListener {
            // Önceki hafta butonuna tıklandığında yapılacak işlemler
            if (binding.barTypeSpinner.selectedItemPosition == 0) {
                // Haftalık görünümdeyiz, haftayı bir önceki haftaya kaydır
                currentWeek++
                calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)
                updateWeekDatesText()
            } else {
                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth++
                if (currentMonth > 12) {
                    currentMonth = 1 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear++
                }
                calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                updateMonthYearText()
            }
        }


    }


    // Kategoriye göre renk atayan yardımcı fonksiyon
    private fun getColorForCategory(categoryName: String): Int {
        return when (categoryName) {
            "Utils" -> Color.GREEN
            "Transport" -> Color.BLUE
            "Rent" -> Color.RED
            "Food&Grocery" -> Color.CYAN
            else -> Color.parseColor("#FFA500") // Diğer kategoriler için turuncu renk
        }
    }

    private fun calculateAndDisplayGeneralExpenses(pieChart: PieChart){

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }

        expenses?.let { expenseAdapter.updateExpenseList(it) }

        val categoryTotals = mutableMapOf<String, Float>()

        expenses?.forEach { expense ->
            val categoryName = expense.categoryName
            val amount = categoryTotals.getOrDefault(categoryName, 0f)
            categoryTotals[categoryName] = amount + expense.amount.toFloat()
        }

        val entries = mutableListOf<PieEntry>()

        // Her kategori için toplam miktarı pie chart'a ekle
        for ((categoryName, totalAmount) in categoryTotals) {
            entries.add(PieEntry(totalAmount, categoryName))
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Expense")

        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map { entry ->
            getColorForCategory(entry.label)
        }

        //centertext
        val totalExpense = expenses?.sumOf { it.amount.toDouble() }

        val centerText = "Total Expense:\n${totalExpense ?: 0.0} USD"
        pieChart.centerText = centerText

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()

    }



    private fun calculateAndDisplayMonthlyExpenses(pieChart: PieChart) {
        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }

        expenses?.let { expenseAdapter.updateExpenseList(it) }

        val monthYearTotals = mutableMapOf<String, Float>()

        // currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Months are 0-based in Calendar class

        expenses?.forEach { expense ->
            val expenseMonth = getMonthFromDate(expense.date)

            if (expenseMonth == currentMonth) {
                val categoryName = expense.categoryName
                val amount = monthYearTotals.getOrDefault(categoryName, 0f)
                monthYearTotals[categoryName] = amount + expense.amount.toFloat()
            }
        }

        val entries = mutableListOf<PieEntry>()

        // Belirli bir ay için toplam miktarı pie chart'a ekle
        for ((categoryName, totalAmount) in monthYearTotals) {
            entries.add(PieEntry(totalAmount, categoryName))
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Expense")

        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map { entry ->
            getColorForCategory(entry.label)
        }

        // Centertext
        val totalExpense = monthYearTotals.values.sum()
        val centerText = "Total Expense:\n${totalExpense} USD"
        pieChart.centerText = centerText

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()


    }

    // Tarih formatından ayı almak için yardımcı bir fonksiyon
    private fun getMonthFromDate(date: String): Int {
        val parts = date.split("-")
        return parts[1].toInt()
    }







    private fun calculateAndDisplayWeeklyExpenses(barChart: BarChart) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }
        val calendar = Calendar.getInstance()

        val weeklyExpensesMap = HashMap<Int, Float>()

        // Harcamaları haftalara göre grupla
        expenses?.forEach { expense ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(expense.date)
            calendar.time = date
            val expenseWeek = calendar.get(Calendar.WEEK_OF_YEAR)

            if (expenseWeek == currentWeek) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val amount = weeklyExpensesMap[dayOfWeek] ?: 0f
                weeklyExpensesMap[dayOfWeek] = amount + expense.amount.toFloat()
            }
        }

        // Eksik günler için 0 değeri ekle
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            if (!weeklyExpensesMap.containsKey(i)) {
                weeklyExpensesMap[i] = 0f
            }
        }

        // Haftanın günlerine göre harcamaları görselleştir
        displayWeeklyExpenses(barChart, weeklyExpensesMap)
    }
    // Haftalık harcamaları görselleştiren bir yardımcı işlev
    // Haftalık harcamaları görselleştiren bir yardımcı işlev
    private fun displayWeeklyExpenses(barChart: BarChart, weeklyExpensesMap: HashMap<Int, Float>) {
        val dayAbbreviations = hashMapOf(
            Calendar.SUNDAY to "Sun",
            Calendar.MONDAY to "Mon",
            Calendar.TUESDAY to "Tue",
            Calendar.WEDNESDAY to "Wed",
            Calendar.THURSDAY to "Thu",
            Calendar.FRIDAY to "Fri",
            Calendar.SATURDAY to "Sat"
        )

        val barEntries = ArrayList<BarEntry>()
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            barEntries.add(BarEntry(i.toFloat(), weeklyExpensesMap[i]!!))
        }

        val dataSet = BarDataSet(barEntries, "Weekly Expenses")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart.data = barData
        barChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val dayOfWeek = value.toInt()
                return dayAbbreviations[dayOfWeek] ?: ""
            }
        }

        barChart.invalidate()
    }



    private fun chart_select() {
        binding.barTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            binding.expensePieChart.visibility = View.GONE
                            binding.expenseBarChart.visibility = View.VISIBLE
                            calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)
                            updateWeekDatesText()
                        }

                        1 -> {

                            binding.expensePieChart.visibility = View.VISIBLE
                            binding.expenseBarChart.visibility = View.GONE
                            calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                            updateMonthYearText()
                        }
                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

    }

    private fun updateWeekDatesText() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek)
        calendar.firstDayOfWeek = Calendar.MONDAY // Yılın ilk gününü Pazartesi olarak ayarla

        // İlk günü ayarla
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        val startDate = calendar.time

        // Son günü ayarla
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val endDate = calendar.time

        val dateFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        val formattedStartDate = dateFormat.format(startDate)
        val formattedEndDate = dateFormat.format(endDate)

        val weekDatesText = "$formattedStartDate - $formattedEndDate"
        binding.weekDatesTextView.text = weekDatesText
    }

    private fun updateMonthYearText() {

        val monthYearText = "${currentMonth}. $currentYear"
        binding.weekDatesTextView.text = monthYearText
    }




}


