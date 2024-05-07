package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.mertyigit0.budgetmanager.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.adapters.IncomeAdapter
import com.mertyigit0.budgetmanager.adapters.IncomeSwipeToDeleteCallback
import com.mertyigit0.budgetmanager.data.CombinedIncome
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income
import com.mertyigit0.budgetmanager.data.RegularIncome
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeBinding
import com.mertyigit0.budgetmanager.util.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeAdapter: IncomeAdapter


    private var currentYear: Int = 0 // Mevcut yılı saklamak için değişken
    private var currentMonth: Int = 0 // Mevcut ayı saklamak için değişken
    private var currentWeek: Int = 0 // Mevcut haftanın numarasını saklamak için değişken



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        auth = FirebaseAuth.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentIncomeBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        incomeAdapter = IncomeAdapter(requireContext(), ArrayList(),navController) // Boş bir ArrayList ile IncomeAdapter oluştur

        val recyclerView = binding.incomeRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = incomeAdapter


        currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Mevcut ayı al (1 ile başlar, 0-based değil)
        currentYear = Calendar.getInstance().get(Calendar.YEAR) // Mevcut yılı al




        // ItemTouchHelper'ı kullanarak swipe to delete özelliğini ekleyin
        val itemTouchHelper = ItemTouchHelper(IncomeSwipeToDeleteCallback(incomeAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val incomePieChart: PieChart = binding.incomePieChart
        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val incomes = userData?.let { dbHelper.getAllIncomesByUserId(it.id) }
        val regularIncomes = userData?.let { dbHelper.getAllRegularIncomesByUserId(it.id) }
        val combinedIncomes = regularIncomes?.let { combineIncomesAndRegularIncomes(incomes ?: listOf(), it) }

        // Gelir verilerini RecyclerView'a aktar
       combinedIncomes?.let { incomeAdapter.updateIncomeList(it) }

        calculateAndDisplayMonthlyIncomes(incomePieChart)
        updateMonthYearText()




        binding.toggleButtonGroup.check(R.id.incomesButton)

        binding.expensesButton.setOnClickListener {
            navController.navigate(R.id.action_incomeFragment_to_expenseFragment)
        }
        binding.addIncomebutton.setOnClickListener {
            navController.navigate(R.id.action_incomeFragment_to_addIncomeFragment)
        }
        binding.viewAllButton.setOnClickListener {
            navController.navigate(R.id.action_incomeFragment_to_incomeListFragment)
        }

        binding.buttonPreviousWeek.setOnClickListener {
            // Önceki hafta butonuna tıklandığında yapılacak işlemler

                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth--
                if (currentMonth < 1) {
                    currentMonth = 12 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear--
                }

                calculateAndDisplayMonthlyIncomes(binding.incomePieChart)
                updateMonthYearText()

        }

        binding.nextWeekButton.setOnClickListener {
            // Önceki hafta butonuna tıklandığında yapılacak işlemler

                // Aylık görünümdeyiz, mevcut ayı bir önceki aya kaydır
                currentMonth++
                if (currentMonth > 12) {
                    currentMonth = 1 // 12'den küçükse, yılı bir azalt ve ayı Aralık yap
                    currentYear++
                }
                calculateAndDisplayMonthlyIncomes(binding.incomePieChart)
                updateMonthYearText()
            }


    }

    private fun updateMonthYearText() {

        val monthYearText = "${currentMonth}. $currentYear"
        binding.weekDatesTextView.text = monthYearText
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



    private fun calculateAndDisplayMonthlyIncomes(pieChart: PieChart) {
        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        val incomes = userData?.let { dbHelper.getAllIncomesByUserId(it.id) }
        val regularIncomes = userData?.let { dbHelper.getAllRegularIncomesByUserId(it.id) }
        val combinedIncomes = regularIncomes?.let { combineIncomesAndRegularIncomes(incomes ?: listOf(), it) }

        combinedIncomes?.let { incomeAdapter.updateIncomeList(it) }

        val monthYearTotals = mutableMapOf<String, Float>()

        combinedIncomes?.forEach { income ->
            val incomeMonth = getMonthFromDate(income.date)

            if (incomeMonth == currentMonth) {
                val categoryName = income.categoryName
                val amount = monthYearTotals.getOrDefault(categoryName, 0f)
                monthYearTotals[categoryName] = amount + income.amount.toFloat()
            }
        }

        val entries = mutableListOf<PieEntry>()

        // Belirli bir ay için toplam miktarı pie chart'a ekle
        for ((categoryName, totalAmount) in monthYearTotals) {
            val categoryID = dbHelper.getIncomeCategoryIdByCategoryName(categoryName) // Kategori adından ID'yi alabilirsiniz, kendi projenize göre uyarlayın
            val entryLabel = "$categoryName ($categoryID)" // Kategori adı ve ID'sini birleştirin
            entries.add(PieEntry(totalAmount, entryLabel))
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Income")

        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map { entry ->
            getColorForCategory(entry.label)
        }

        // Centertext
        val totalIncome = monthYearTotals.values.sum()
        val formattedTotalIncome = String.format("%.2f", totalIncome)
        val userCurrency = getUserCurrency()
        val centerText = "Total Income:\n$formattedTotalIncome $userCurrency"
        pieChart.centerText = centerText

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()
    }



    private fun getUserCurrency(): String {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        return currentUserEmail?.let { dbHelper.getUserData(it)?.currency } ?: "USD" // Varsayılan olarak USD kullan
    }
    // Tarih formatından ayı almak için yardımcı bir fonksiyon
    private fun getMonthFromDate(date: String): Int {
        val parts = date.split("-")
        return parts[1].toInt()
    }

    fun combineIncomesAndRegularIncomes(incomes: List<Income>, regularIncomes: List<RegularIncome>): List<CombinedIncome> {
        val combinedList = mutableListOf<CombinedIncome>()

        // Gelirleri CombinedIncome türüne dönüştür ve birleştir
        incomes.forEach { income ->
            combinedList.add(
                CombinedIncome(
                    id = income.id,
                    userId = income.userId,
                    title = null,  // RegularIncome alanları null olacak
                    amount = income.amount,
                    currency = income.currency,
                    recurrence = null,  // RegularIncome alanları null olacak
                    date = income.date,
                    categoryId = income.categoryId,
                    categoryName = income.categoryName,
                    note = income.note,
                    createdAt = income.createdAt
                )
            )
        }

        // Regular gelirleri CombinedIncome türüne dönüştür ve birleştir
        regularIncomes.forEach { regularIncome ->
            combinedList.add(
                CombinedIncome(
                    id = regularIncome.id,
                    userId = regularIncome.userId,
                    title = regularIncome.title,
                    amount = regularIncome.amount,
                    currency = regularIncome.currency,
                    recurrence = regularIncome.recurrence,
                    date = regularIncome.date,
                    categoryId = regularIncome.categoryId,
                    categoryName = regularIncome.categoryName,  // RegularIncome'ı diğerlerinden ayırmak için kategori adını belirleyin
                    note = null,  // RegularIncome alanları null olacak
                    createdAt = ""
                )
            )
        }

        return combinedList
    }







}