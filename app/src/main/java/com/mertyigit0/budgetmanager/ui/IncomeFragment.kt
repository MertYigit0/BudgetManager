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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


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

        incomeAdapter = IncomeAdapter(requireContext(), ArrayList()) // Boş bir ArrayList ile IncomeAdapter oluştur

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

        val categoryTotals = mutableMapOf<String, Float>()

        // Her kategori için toplam geliri hesapla
        incomes?.forEach { income ->
            val categoryName = income.categoryName
            val amount = categoryTotals.getOrDefault(categoryName, 0f)
            categoryTotals[categoryName] = amount + income.amount.toFloat()
        }

        val entries = mutableListOf<PieEntry>()

        // Her kategori için toplam geliri pie chart'a ekle
        for ((categoryName, totalAmount) in categoryTotals) {
            entries.add(PieEntry(totalAmount, categoryName))
        }


        //centertext
        val totalIncome = incomes?.sumOf { it.amount.toDouble() }

        val centerText = "Total Income:\n${totalIncome ?: 0.0} USD"
        incomePieChart.centerText = centerText



        // Veri setini oluştur
        val dataSet = PieDataSet(entries, "Income")
        // Kategori renklerini dataSet'e ekle
        dataSet.colors = entries.map {
            getRandomColor()
        }
        // Veri setini PieData'ya ekle
        val pieData = PieData(dataSet)
        // PieChart'a PieData'yı ayarla
        incomePieChart.data = pieData
        // Chart'ın güncellenmesini sağla
        incomePieChart.invalidate()


        val navController = Navigation.findNavController(requireView())
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
    }


    // Kategoriye göre renk atayan yardımcı fonksiyon
    private fun getColorForCategory(categoryId: Float): Int {
        return when (categoryId) {
            1f -> Color.GREEN
            2f -> Color.BLUE
            3f -> Color.RED
            4f -> Color.YELLOW
            5f -> Color.MAGENTA
            6f -> Color.CYAN
            7f -> Color.GRAY
            8f -> Color.LTGRAY
            9f -> Color.BLACK
            else -> Color.parseColor("#FFA500") // Tanımlanmamış kategori id'leri için turuncu renk
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


}