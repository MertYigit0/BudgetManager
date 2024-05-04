package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.HorizontalScrollView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
import kotlin.random.Random


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

        val navController = Navigation.findNavController(requireView())

        expenseAdapter = ExpenseAdapter(
            requireContext(),
            ArrayList(),
            navController

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
            } else if ((binding.barTypeSpinner.selectedItemPosition == 1)) {
                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth--
                if (currentMonth < 1) {
                    currentMonth = 12 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear--
                }

                calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                updateMonthYearText()
            }else if ((binding.barTypeSpinner.selectedItemPosition == 2)) {
                currentYear --
                displayMonthlyIncomeAndExpenses(binding.expenseIncomeBarChart)

            }
        }

        binding.nextWeekButton.setOnClickListener {
            // Önceki hafta butonuna tıklandığında yapılacak işlemler
            if (binding.barTypeSpinner.selectedItemPosition == 0) {
                // Haftalık görünümdeyiz, haftayı bir önceki haftaya kaydır
                currentWeek++
                calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)
                updateWeekDatesText()
            } else if (binding.barTypeSpinner.selectedItemPosition == 1){
                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth++
                if (currentMonth > 12) {
                    currentMonth = 1 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear++
                }
                calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                updateMonthYearText()
            }
            else if ((binding.barTypeSpinner.selectedItemPosition == 2)) {
            currentYear ++
            displayMonthlyIncomeAndExpenses(binding.expenseIncomeBarChart)

        }
        }


        binding.viewAllButton.setOnClickListener {
            navController.navigate(R.id.action_expenseFragment_to_expenseListFragment)
        }








    }


    // Kategoriye göre renk atayan yardımcı fonksiyon
    private fun getColorForCategory(categoryLabel: String): Int {
        val categoryIdStartIndex = categoryLabel.lastIndexOf("(") + 1
        val categoryIdEndIndex = categoryLabel.lastIndexOf(")")
        val categoryIdString = categoryLabel.substring(categoryIdStartIndex, categoryIdEndIndex)
        val categoryId = categoryIdString.trim().toFloat() // Parantez içindeki kategori ID'sini al ve float'a çevir
        // Kategori ID'sine göre renk belirle
        return when (categoryId) {
            // Kategori ID'lerine göre renkleri belirle
            1f ->  ContextCompat.getColor(requireContext(), R.color.chart_green)
            2f ->  ContextCompat.getColor(requireContext(), R.color.chart_blue)
            3f ->  ContextCompat.getColor(requireContext(), R.color.chart_red)
            4f ->  ContextCompat.getColor(requireContext(), R.color.chart_yellow)
            5f -> ContextCompat.getColor(requireContext(), R.color.chart_purple)
            6f -> ContextCompat.getColor(requireContext(), R.color.chart_cyan)
            7f -> ContextCompat.getColor(requireContext(), R.color.chart_gray)
            8f -> ContextCompat.getColor(requireContext(), R.color.chart_orange)
            9f -> Color.BLACK
            else -> getRandomColor() // Tanımlanmamış kategori id'leri için rastgele renk döndür
        }
    }

    // Rastgele renkler üretmek için bir fonksiyon
    fun getRandomColor(): Int {
        // Renkleri oluşturmak için rastgele RGB değerleri alın
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)

        // RGB değerlerini tek bir Int olarak birleştirin ve döndürün
        return (0xFF shl 24) or (r shl 16) or (g shl 8) or b
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
            val categoryID = dbHelper.getExpenseCategoryIdByCategoryName(categoryName) // Kategori adından ID'yi alabilirsiniz, kendi projenize göre uyarlayın
            val entryLabel = "$categoryName ($categoryID)" // Kategori adı ve ID'sini birleştirin
            entries.add(PieEntry(totalAmount, entryLabel))
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
                            binding.expenseIncomeBarChart.visibility = View.GONE
                            binding.expenseBarChart.visibility = View.VISIBLE

                            calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)

                            updateWeekDatesText()
                        }

                        1 -> {

                            binding.expensePieChart.visibility = View.VISIBLE
                            binding.expenseBarChart.visibility = View.GONE
                            binding.expenseIncomeBarChart.visibility = View.GONE

                           calculateAndDisplayMonthlyExpenses(binding.expensePieChart)

                            updateMonthYearText()
                        }
                        2 -> {

                            binding.expensePieChart.visibility = View.GONE
                            binding.expenseBarChart.visibility = View.GONE
                            binding.expenseIncomeBarChart.visibility = View.VISIBLE

                           // calculateAndDisplayMonthlyExpenses(binding.expensePieChart)
                            displayMonthlyIncomeAndExpenses(binding.expenseIncomeBarChart)

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

    private fun updateYearText() {

        val monthYearText = "$currentYear Income Expense Comparison"
        binding.weekDatesTextView.text = monthYearText
    }

    private fun displayMonthlyIncomeAndExpenses(barChart: BarChart) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val monthlyIncomes = ArrayList<Double>()
        val monthlyExpenses = ArrayList<Double>()

        // Yatay kaydırma için gösterilecek maksimum ay sayısı
        val maxVisibleMonths = 12

        for (i in Calendar.JANUARY until Calendar.JANUARY + maxVisibleMonths) {
            val month = i % 12 + 1 // Ay indeksi 0'dan başladığı için +1 ekliyoruz, döngü geçişleri için mod işlemi yapılır
            val monthlyIncome = dbHelper.getTotalIncomeForMonth(userData?.id ?: -1, month)
            val monthlyExpense = dbHelper.getTotalExpenseInMonth(userData?.id ?: -1, month)

            monthlyIncomes.add(monthlyIncome)
            monthlyExpenses.add(monthlyExpense)
        }
        updateYearText()

        val incomeDataSet = BarDataSet(monthlyIncomes.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }, "Income")
        val expenseDataSet = BarDataSet(monthlyExpenses.mapIndexed { index, value -> BarEntry(index.toFloat(), value.toFloat()) }, "Expense")

        // Gelir ve gider renkleri
        incomeDataSet.color = ContextCompat.getColor(requireContext(), R.color.chart_green)
        expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.chart_red)

        val barData = BarData(incomeDataSet, expenseDataSet)
        barData.barWidth = 0.35f // Barların genişliği
        barChart.data = barData

        // Ay isimlerini ayarlayın
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Yatay kaydırma özelliklerini ayarlayın
        barChart.setPinchZoom(true) // Yakınlaştırma ve kaydırma özelliğini etkinleştirir
        barChart.isDragEnabled = true // Kaydırmayı etkinleştirir
        barChart.setVisibleXRangeMaximum(maxVisibleMonths.toFloat()) // Aynı anda gösterilecek maksimum ay sayısını belirler
        barChart.moveViewToX(0f) // Başlangıç pozisyonunu ayarlar

        barChart.groupBars(0f, 0.08f, 0.03f) // Gruplanmış veri setlerini ayarlar

        barChart.invalidate()
    }






}


