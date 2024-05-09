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
import org.apache.commons.math3.fitting.WeightedObservedPoints
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

        if (userData != null) {
            predictForMonths(userData.id)
        }
        //zart()
        zo()
    }



    // Yeni FinancialGoal eklemek için AlertDialog gösteren fonksiyon
    /*
    private fun showAddFinancialGoalDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.financial_goal_alert_dialog, null)
        builder.setView(dialogView)
            .setTitle("Add Financial Goal")
            .setPositiveButton("Add") { dialog, which ->
                val titleEditText: EditText = dialogView.findViewById(R.id.titleEditText)
                val descriptionEditText: EditText = dialogView.findViewById(R.id.descriptionEditText)
                val targetAmountEditText: EditText = dialogView.findViewById(R.id.targetAmountEditText)
                val deadlineEditText: EditText = dialogView.findViewById(R.id.deadlineEditText)

                // Kullanıcının girdiği bilgileri al
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val targetAmount = targetAmountEditText.text.toString().toDouble()
                val deadline = deadlineEditText.text.toString()
                val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
                // Yeni bir FinancialGoal nesnesi oluştur
                val newFinancialGoal = FinancialGoal(
                    id = financialGoals.size + 1,
                    userId = userId, // Kullanıcı kimliğini buraya ekleyin
                    title = title,
                    description = description,
                    targetAmount = targetAmount,
                    currentAmount = 0.0, // Başlangıçta 0 olarak ayarlanabilir
                    deadline = deadline,
                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    categoryId =
                )

                // Oluşturulan hedefi listeye ekle
                financialGoals.add(newFinancialGoal)
                adapter.notifyDataSetChanged()


                addFinancialGoalToDatabase(newFinancialGoal)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }*/


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

    fun predictForMonths(userId: Int) {
        val (allIncomeData, allExpenseData) = getAllIncomesAndExpensesForMonths(userId, 1, 6) // 1'den 6'ya kadar olan ayların verilerini al

        val linearRegression = LinearRegression()

        for (month in 7..9) {
            // Tahmin yapılacak gün olarak ayın başlangıcı (1. gün) kullanılıyor
            val targetDay = 1.0
            val predictedValue = calculatePrediction(allIncomeData, allExpenseData, targetDay)
            println("Tahminler $month. ay için: $predictedValue")
        }
    }

    fun calculatePrediction(incomeAmounts: List<Double>, expenseAmounts: List<Double>, targetDay: Double): Double {
        val regression = SimpleRegression()

        // Gelir verilerini modele ekle
        incomeAmounts.forEachIndexed { index, amount ->
            regression.addData(index.toDouble(), amount)
        }

        // Gider verilerini modele ekle
        expenseAmounts.forEachIndexed { index, amount ->
            regression.addData(index.toDouble(), -amount) // Giderler negatif olarak eklenir
        }

        return regression.predict(targetDay)
    }



    fun zart() {
        val incomes = doubleArrayOf(1000.0, 900.0, 800.0, 700.0, 600.0, 500.0)
        val expenses = doubleArrayOf(500.0, 600.0, 700.0, 800.0, 9100.0, 4000.0)

        val regression = SimpleRegression()

        // Gelir ve gider verilerini modele ekle
        for (i in incomes.indices) {
            regression.addData(i.toDouble() + 1, incomes[i])
            regression.addData((i.toDouble() + 1) + 0.5, -expenses[i]) // Giderler negatif olarak eklenir ve ofset eklenir
        }

        // 7. ay için tahmin yap
        val predictedIncome = regression.predict(7.0)
        val predictedExpense = -regression.predict(7.0) // Giderler negatif olarak eklendiği için negatif olarak alınmalı

        println("7. ay için tahmin edilen gelir: $predictedIncome")
        println("7. ay için tahmin edilen gider: $predictedExpense")
    }


    fun zo() {
        // Geçmiş gelir verileri (örnek olarak son 10 gün)
        val gunler = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
        val gelirler = doubleArrayOf(100.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0)

        // Veri noktalarını ağırlıklı olarak kaydedin
        val veriNoktalari = WeightedObservedPoints()
        for (i in gunler.indices) {
            veriNoktalari.add(gunler[i], gelirler[i])
        }

        // Polinom eğrisi uyumlamak için bir eğri uyumlayıcı oluşturun
        val egriliUyumlayici = PolynomialCurveFitter.create(2)

        // Polinom eğrisini uyumla ve katsayıları al
        val katsayilar = egriliUyumlayici.fit(veriNoktalari.toList())

        // Son gününüze göre gelecek 5 günün gelir tahminini yapın
        val sonGun = gunler.last()
        val gelecekGunler = doubleArrayOf(sonGun + 1, sonGun + 2, sonGun + 3, sonGun + 4, sonGun + 5)
        val tahminEdilenGelirler = PolynomialFunction(katsayilar).value(gelecekGunler[0]) // Sadece bir gün tahmini için

        // Tahmin edilen gelecek günlerin gelirlerini yazdırın
        println("Gelecek günün Tahmini Geliri: $tahminEdilenGelirler")
    }




}