package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeBinding


class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeAdapter: IncomeAdapter

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

        incomeAdapter = IncomeAdapter(requireContext(),ArrayList()) // Boş bir ArrayList ile IncomeAdapter oluştur

        val recyclerView = binding.incomeRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = incomeAdapter

        // ItemTouchHelper'ı kullanarak swipe to delete özelliğini ekleyin
        val itemTouchHelper = ItemTouchHelper(IncomeSwipeToDeleteCallback(incomeAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)


        val incomePieChart: PieChart = binding.incomePieChart
        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val incomes = userData?.let { dbHelper.getAllIncomesByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
        incomes?.let { incomeAdapter.updateIncomeList(it) }
        // Gelir verileri listesini oluştur
        val entries = mutableListOf<PieEntry>()

        if (incomes != null) {
            incomes?.forEach { income ->
                entries.add(PieEntry(income.amount.toFloat(), income.categoryId.toString()))
            }
        }

        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Gelir")
        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map { entry ->
            getColorForCategory(entry.label.toInt())
        }
        // Veri setini PieData'ya ekle
        val pieData = PieData(dataSet)
        // PieChart'a PieData'yı ayarla
        incomePieChart.data = pieData
        // Chart'ın güncellenmesini sağla
        incomePieChart.invalidate()

        val navController = Navigation.findNavController(requireView())
        binding.toggleButtonGroup.check(R.id.incomesButton)




        binding.expensesButton.setOnClickListener{
            navController.navigate(R.id.action_incomeFragment_to_expenseFragment)
        }
        binding.addIncomebutton.setOnClickListener{
            navController.navigate(R.id.action_incomeFragment_to_addIncomeFragment)
        }
        binding.viewAllButton.setOnClickListener{
            navController.navigate(R.id.action_incomeFragment_to_incomeListFragment)
        }


    }

    // Kategoriye göre renk atayan yardımcı fonksiyon
    private fun getColorForCategory(categoryId: Int): Int {
        return when (categoryId) {
            1 -> Color.GREEN
            2 -> Color.BLUE
            3 -> Color.RED
            4 -> Color.YELLOW
            5 -> Color.MAGENTA
            6 -> Color.CYAN
            7 -> Color.GRAY
            8 -> Color.LTGRAY
            9 -> Color.BLACK
            else -> Color.parseColor("#FFA500") // Tanımlanmamış kategori id'leri için turuncu renk
        }
    }



}