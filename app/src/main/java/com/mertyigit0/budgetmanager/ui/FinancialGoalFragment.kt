package com.mertyigit0.budgetmanager.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.FinancialGoalAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentAddExpenseBinding
import com.mertyigit0.budgetmanager.databinding.FragmentFinancialGoalBinding
import com.mertyigit0.budgetmanager.util.LinearRegression
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.SimpleCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoints
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class FinancialGoalFragment : Fragment() {

    private var _binding:FragmentFinancialGoalBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var auth: FirebaseAuth

    private val financialGoals = mutableListOf<FinancialGoal>()
    private lateinit var adapter: FinancialGoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFinancialGoalBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var dbHelper = DatabaseHelper(requireContext())

        // RecyclerView'i bul
        val recyclerView: RecyclerView = binding.financialGoalRecyclerView
        val navController = Navigation.findNavController(requireView())
        // Layout yöneticisini ayarla (Dikey olarak sıralama)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter oluşturulmadan önce öncelikle başlatılmalı
        adapter = FinancialGoalAdapter(requireContext(),financialGoals,navController)

        // RecyclerView'e adapter'ı bağla
        recyclerView.adapter = adapter
       val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

        // Kullanıcının finansal hedeflerini al
        currentUserEmail?.let { email ->
            val userData = dbHelper.getUserData(email)
            userData?.let { user ->
                val goals = dbHelper.getAllFinancialGoalsByUserId(user.id)
                financialGoals.addAll(goals)
                adapter.notifyDataSetChanged()
            }
        }

        // Adapter oluşturulduktan sonra kullanılabilir
        binding.addFinancialGoalButton.setOnClickListener{
            navController.navigate(R.id.action_financialGoalFragment_to_addFinancialGoalFragment)

        }





        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }




        userData?.let { user ->
            val incomesAndRegularIncomesList = dbHelper.getIncomesAndRegularIncomesToList(user.id)
            incomesAndRegularIncomesList.forEachIndexed { index, dailyIncome ->
                println("Day ${index + 1}: $dailyIncome")
            }
        }

     //   main()
    }






    private fun addFinancialGoalToDatabase(financialGoal: FinancialGoal) {
        val dbHelper = DatabaseHelper(requireContext())
        val success = dbHelper.addFinancialGoal(financialGoal)
        if (success) {


        } else {

        }
    }

/*
    fun getIncomeAndExpenseData() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        val incomes = userData?.let { dbHelper.getAllIncomesByUserId(it.id) }
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }

        val incomeAmounts = incomes?.map { it.amount } ?: emptyList()
        val expenseAmounts = expenses?.map { it.amount } ?: emptyList()

        val linearRegression = LinearRegression()

        // Hedef gün için tahmin yap
        val targetDay = 2.0
        val predictedValue = calculatePrediction(incomeAmounts, expenseAmounts, targetDay)

        println("Hedef gün $targetDay için tahmin edilen değer: $predictedValue")
    }

*/
    fun getAllIncomesAndExpensesForMonths(userId: Int, startMonth: Int, endMonth: Int): Pair<List<Double>, List<Double>> {

        val dbHelper = DatabaseHelper(requireContext()) // DatabaseHelper sınıfının bir örneğini oluştur
        val allIncomeData = mutableListOf<Double>()
        val allExpenseData = mutableListOf<Double>()

        for (month in startMonth..endMonth) {
            val incomes = dbHelper.getAllIncomesForMonth(userId, month)
            val expenses = dbHelper.getAllExpensesForMonth(userId, month)

            incomes.forEach { income ->
                allIncomeData.add(income.amount)
            }

            expenses.forEach { expense ->
                allExpenseData.add(expense.amount)
            }
        }

        return Pair(allIncomeData, allExpenseData)
    }









    fun main() {
        // Kullanıcıların günlere göre gelirlerini içeren bir liste
        val dailyIncomes = listOf(500.0, 400.0, 70.0, 30.0, 10.0, 80.0, 50.0, 900.0)

        // Toplam biriktirilmek istenen değer
        val targetAmount = 10000.0

        // Apache Common Maths kütüphanesini kullanarak basit lineer regresyon modeli oluştur
        val regression = SimpleRegression()

        // Günlük gelir verilerini regresyon modeline ekle
        for (i in dailyIncomes.indices) {
            regression.addData(i.toDouble(), dailyIncomes[i])
        }

        // Regresyon modelini kullanarak gelecek günlerdeki gelirleri tahmin et
        val nextDayIndex = dailyIncomes.size
        val nextDayIncome = regression.predict(nextDayIndex.toDouble())

        // Önceki günlerdeki gelirlerin toplamının 1000 TL'ye ulaşacağı tahmini gün sayısı
        var totalIncome = 0.0
        var days = 0
        while (totalIncome < targetAmount) {
            totalIncome += dailyIncomes.getOrElse(days) { nextDayIncome }
            days++
        }

        // Tahmini gün sayısından mevcut gün sayısını çıkararak gelecekte kaç gün olduğunu bul
        val futureDays =  days - dailyIncomes.size

        println("Önceki günlerdeki gelirlerin toplamının 1000 TL'ye ulaşacağı tahmini gün sayısı: $days gün")
        println("Gelecekteki gün sayısı: $futureDays gün")
    }


}