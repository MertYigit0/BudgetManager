package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.ExpenseAdapter

import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseBinding



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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentExpenseBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       expenseAdapter = ExpenseAdapter(ArrayList()) // Boş bir ArrayList ile ExpenseAdapter oluştur

        val recyclerView = binding.expenseRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

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
        binding.incomesButton.setOnClickListener{
            navController.navigate(R.id.action_expenseFragment_to_incomeFragment)
        }

        binding.addExpensebutton.setOnClickListener{
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


}