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
        val expenseBarChart: BarChart = binding.expenseBarChart
        // Haftanın günlerine göre harcamaları hesapla ve bar chart'ı güncelle
      //  calculateAndDisplayWeeklyExpenses(expenseBarChart)

        chart_select()

        expenseAdapter = ExpenseAdapter(
            requireContext(),
            ArrayList()
        ) // Boş bir ArrayList ile ExpenseAdapter oluştur

        val recyclerView = binding.expenseRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

        // ItemTouchHelper'ı kullanarak swipe to delete özelliğini ekleyin
        val itemTouchHelper = ItemTouchHelper(ExpenseSwipeToDeleteCallback(expenseAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val expensePieChart: PieChart = binding.expensePieChart
        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
        expenses?.let { expenseAdapter.updateExpenseList(it) }
        // Gelir verileri listesini oluştur
        val entries = mutableListOf<PieEntry>()

        if (expenses != null) {
            expenses.forEach { expense ->
                entries.add(PieEntry(expense.amount.toFloat(), expense.categoryName))
            }
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Expense")
        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map { entry ->
            getColorForCategory(entry.label)
        }
        // Veri setini PieData'ya ekle
        val pieData = PieData(dataSet)
        // PieChart'a PieData'yı ayarla
        expensePieChart.data = pieData
        // Chart'ın güncellenmesini sağla
        expensePieChart.invalidate()


        val navController = Navigation.findNavController(requireView())
        binding.toggleButtonGroup.check(R.id.expensesButton)
        binding.incomesButton.setOnClickListener {
            navController.navigate(R.id.action_expenseFragment_to_incomeFragment)
        }

        binding.addExpensebutton.setOnClickListener {
            navController.navigate(R.id.action_expenseFragment_to_addExpenseFragment)
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

    private fun calculateAndDisplayWeeklyExpenses(barChart: BarChart) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }

        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

        val weeklyExpensesMap =
            HashMap<Int, Float>() // Haftanın günlerine göre harcamaları saklamak için bir map

        // Haftanın günlerine göre harcamaları hesapla
        expenses?.forEach { expense ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(expense.date) // String tarihini Date türüne dönüştür
            calendar.time = date
            val expenseWeek = calendar.get(Calendar.WEEK_OF_YEAR)

            if (expenseWeek == currentWeek) { // Harcama bu haftadaysa
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val amount = weeklyExpensesMap[dayOfWeek] ?: 0f // Günlük harcamayı al (varsa)
                weeklyExpensesMap[dayOfWeek] =
                    amount + expense.amount.toFloat() // Günlük harcamayı güncelle
            }
        }

        // Haftanın her gününü kontrol et ve harcama olmayan günler için 0 değeri ekle
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            if (!weeklyExpensesMap.containsKey(i)) {
                weeklyExpensesMap[i] = 0f
            }
        }

        // Haftanın günlerine göre harcamaları bar chart'a ekle
        val barEntries = ArrayList<BarEntry>()
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            barEntries.add(BarEntry(i.toFloat(), weeklyExpensesMap[i]!!))
        }

        // BarDataSet oluştur
        val dataSet = BarDataSet(barEntries, "Haftalık Harcamalar")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()

        // BarData oluştur
        val barData = BarData(dataSet)
        barData.barWidth = 0.9f // Bar genişliğini ayarla

        // BarChart'a BarData'yı ayarla
        barChart.data = barData

        // Chart'ın güncellenmesini sağla
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
                            binding.expensePieChart.visibility = View.VISIBLE
                            binding.expenseBarChart.visibility = View.GONE
                            // Haftalık harcamaları göster

                        }

                        1 -> {
                            binding.expensePieChart.visibility = View.GONE
                            binding.expenseBarChart.visibility = View.VISIBLE
                            // Genel harcamaları göster
                            calculateAndDisplayWeeklyExpenses(binding.expenseBarChart)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }


    }
}


