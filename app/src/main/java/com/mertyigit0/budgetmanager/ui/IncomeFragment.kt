package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mertyigit0.budgetmanager.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeBinding


class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null;
    private val binding get() = _binding!!;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentIncomeBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val incomePieChart: PieChart = binding.incomePieChart
        val databaseHelper = DatabaseHelper(requireContext())
        // Veritabanından tüm gelirleri al
        val incomes = databaseHelper.getAllIncomes()

        // Gelir verileri listesini oluştur
        val entries = mutableListOf<PieEntry>()
        incomes.forEach { income ->
            entries.add(PieEntry(income.amount.toFloat(), income.categoryId))
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Gelir")
        dataSet.colors = listOf(Color.BLUE, Color.GREEN, Color.RED) // Veri noktalarının renklerini ayarla

        // Veri setini PieData'ya ekle
        val pieData = PieData(dataSet)

        // PieChart'a PieData'yı ayarla
        incomePieChart.data = pieData

        // Chart'ın güncellenmesini sağla
        incomePieChart.invalidate()

        binding.addIncomeButton.setOnClickListener{
            val navController = findNavController()
            navController.navigate(R.id.addIncomeFragment)
        }
    }



}