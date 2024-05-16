package com.mertyigit0.budgetmanager.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentAddExpenseBinding
import com.mertyigit0.budgetmanager.databinding.FragmentFinancialGoalDetailBinding
import org.apache.commons.math3.stat.regression.SimpleRegression


class FinancialGoalDetailFragment : Fragment() {

    private var _binding: FragmentFinancialGoalDetailBinding? = null;
    private val binding get() = _binding!!;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFinancialGoalDetailBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         val dbHelper = DatabaseHelper(requireContext())
        val financialGoalId = arguments?.getInt("financialGoalId")
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        // Kullanıcı verileri ve finansal hedef ID'si null değilse işleme devam et
        if (userData != null && financialGoalId != null) {
            // Finansal hedefin günlük gelir listesini al
            val dailyIncomeList = dbHelper.getDailyIncomeForFinancialGoalById(userData.id, financialGoalId)

            // Günlük gelir listesi boş değilse işleme devam et
            if (dailyIncomeList.isNotEmpty()) {
                // Günlük gelir listesinden gelirleri alarak yeni bir liste oluştur
                val incomeList = mutableListOf<Double>()
                for ((_, income) in dailyIncomeList) {
                    incomeList.add(income)
                }

                // FinancialGoalId'yi kullanarak finansal hedefi getir
                val financialGoal = dbHelper.getFinancialGoalById(financialGoalId)

                // Finansal hedef bulunduysa ve null değilse işleme devam et
                if (financialGoal != null) {
                    // LineChart'ı güncelle
                    predictLineChart(dailyIncomeList, incomeList, financialGoal)

                    // Ortalama gelir tahmini fonksiyonunu çağır ve tahmini gün sayısını hesapla
                    val averageDays = averageIncomeForecast(incomeList, financialGoal.targetAmount)
                }
            }
        }


        val financialGoal = financialGoalId?.let { dbHelper.getFinancialGoalById(it) }
        if (financialGoal != null) {
            binding.TargetAmountText.text = "Target Amount: ${financialGoal.targetAmount}${financialGoal.currency} "
        }
        if (financialGoal != null) {
            binding.currentAmountText.text = "Current Amount: ${financialGoal.currentAmount}${financialGoal.currency} "
        }




    }


    fun predictLineChart(
        dailyIncomeList: List<Pair<String, Double>>,
        incomeList: List<Double>,
        financialGoal: FinancialGoal
    ) {
        // LineChart'ı tanımla ve görünürlüğünü ayarla
        val lineChart: LineChart = binding.lineChart
        lineChart.visibility = View.VISIBLE

        // LineChart'a verileri ekle
        val entries: ArrayList<Entry> = ArrayList()
        for ((index, income) in dailyIncomeList.withIndex()) {
            entries.add(Entry(index.toFloat(), income.second.toFloat()))
        }
        val dataSet = LineDataSet(entries, "Current Amount")
        val data = LineData(dataSet)
        lineChart.data = data

        // Hedefe ulaşılmasına kaç gün kaldığını hesapla
        val targetAmount = financialGoal.targetAmount
        val futureDays = linearRegressionForecast(incomeList, targetAmount)

        // LineChart'a hedefe ulaşılmasına kaç gün kaldığını gösteren bir çizgi ekle
        val futureEntries: ArrayList<Entry> = ArrayList()
        for (i in dailyIncomeList.indices) {
            futureEntries.add(
                Entry(
                    i.toFloat(),
                    if (i < dailyIncomeList.size - futureDays) incomeList[i].toFloat() else Float.NaN
                )
            )
        }
        val futureDataSet = LineDataSet(futureEntries, "Days to Reach Target")
        futureDataSet.color = Color.RED
        futureDataSet.setDrawCircles(false)
        lineChart.data.addDataSet(futureDataSet)

        // LineChart'ı güncelle
        lineChart.invalidate()
        updatePredictedDays(financialGoal)
    }

/*
    private fun linearRegressionForecast(dailyIncomes: List<Double>, targetAmount: Double): Int {
        // Apache Common Maths kütüphanesini kullanarak basit lineer regresyon modeli oluştur
        val regression = SimpleRegression()

        // Günlük gelir verilerini regresyon modeline ekle
        for (i in dailyIncomes.indices) {
            regression.addData(i.toDouble(), dailyIncomes[i])
        }

        // Regresyon modelini kullanarak gelecek günlerdeki gelirleri tahmin et
        val nextDayIndex = dailyIncomes.size
        val nextDayIncome = regression.predict(nextDayIndex.toDouble())

        // Önceki günlerdeki gelirlerin toplamının hedef tutarı ulaşacağı tahmini gün sayısı
        var totalIncome = 0.0
        var days = 0
        while (totalIncome < targetAmount) {
            totalIncome += dailyIncomes.getOrElse(days) { nextDayIncome }
            days++
        }

        // Eğim negatif mi kontrol et
        if (regression.slope < 0) {
            // Negatif eğim durumu için kullanıcıya uyarı ver
            binding.EstimatedDayRegression.text = "Estimated Days: Uncertain due to decreasing income trend"
        } else {
            // Eğim pozitif ise veya sıfırsa tahmini gün sayısını göster
            if (dailyIncomes.size < 10) {
                binding.EstimatedDayRegression.text = "Estimated Days: Not enough data for forecasting (Linear Regression)"
            } else {
                binding.EstimatedDayRegression.text = "Estimated Days: $days (Linear Regression)"
            }
        }

        return days - dailyIncomes.size
    }

 */
private fun linearRegressionForecast(dailyIncomes: List<Double>, targetAmount: Double): Int {
    // Apache Common Maths kütüphanesini kullanarak basit lineer regresyon modeli oluştur
    val regression = SimpleRegression()

    // Gelir verileri yeterli mi kontrol et
    if (dailyIncomes.size < 10) {
        println("aaaaaaaaaaa"+regression.slope) // Eğim değerini yazdırın
        return -2 // -2 kodu, yetersiz veri durumunu temsil eder
    }

    // Günlük gelir verilerini regresyon modeline ekle
    for (i in dailyIncomes.indices) {
        regression.addData(i.toDouble(), dailyIncomes[i])
    }

    if (regression.slope < 0) {
        println("aaaaaaaaaaa"+regression.slope) // Eğim değerini yazdırın
        return -1 // -1 kodu, negatif eğim durumunu temsil eder
    }

    // Regresyon modelini kullanarak gelecek günlerdeki gelirleri tahmin et
    val nextDayIndex = dailyIncomes.size
    val nextDayIncome = regression.predict(nextDayIndex.toDouble())

    /*
    for (i in 1..100) { // İleriki
        val nextDayIncome = regression.predict(nextDayIndex.toDouble() + i)
        println("Gün ${nextDayIndex + i}: ${nextDayIncome}")
    }

     */

    // Önceki günlerdeki gelirlerin toplamının hedef tutarı ulaşacağı tahmini gün sayısı
    var totalIncome = 0.0
    var days = 0
    while (totalIncome < targetAmount) {
        totalIncome += dailyIncomes.getOrElse(days) { nextDayIncome }
        days++
    }

    // Eğim negatif mi kontrol et


    // Tahmini gün sayısından mevcut gün sayısını çıkararak gelecekte kaç gün olduğunu bul
    return days - dailyIncomes.size

}

    private fun updatePredictedDays(financialGoal: FinancialGoal) {
        val financialGoalId = financialGoal.id
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        if (userData != null) {
            val dailyIncomeList = dbHelper.getDailyIncomeForFinancialGoalById(userData.id, financialGoalId)

            println("Daily Income List: $dailyIncomeList") // dailyIncomeList'i yazdır

            if (dailyIncomeList.isEmpty()) {
               binding.EstimatedDayRegression.text = "No income data available" // Gelir verisi bulunamadı uyarısı
                return
            }

            val incomeList = mutableListOf<Double>()

            for ((_, income) in dailyIncomeList) {
                incomeList.add(income)
            }

            println("Income List: $incomeList") // incomeList'i yazdır
            // Kontrol eklendi
            if (incomeList.isEmpty() || incomeList.sum() == 0.0) {
                binding.EstimatedDayRegression.text = "No income available or total income is zero" // Gelir verisi bulunamadı uyarısı
                return
            }


            // Kontrol eklendi
            if (incomeList.size < 10) {
                binding.EstimatedDayRegression.text = "Not enough data for prediction"
            } else {
                val targetAmount = financialGoal.targetAmount
                val futureDays = linearRegressionForecast(incomeList, targetAmount)
                if (futureDays == -1) {
                    binding.EstimatedDayRegression.text = "Uncertain prediction due to decreasing income trend"
                } else if (futureDays == -2) {
                    binding.EstimatedDayRegression.text = "Not enough data for prediction"
                } else {
                    binding.EstimatedDayRegression.text = "Approximately Days: $futureDays"
                }
            }
        }
    }


    private fun averageIncomeForecast(dailyIncomes: List<Double>, targetAmount: Double): Int {
        // Günlük gelirlerin ortalama değerini hesapla
        val averageIncome = dailyIncomes.average()

        // Ortalama gelire ulaşmak için gereken gün sayısını hesapla
        val daysToReachTarget = (targetAmount / averageIncome).toInt()

        // Tahmini gün sayısını metin görüntüleyicisine atayarak döndür
        if (dailyIncomes.size < 3) {
            binding.EstimatedDayAverage.text = "Estimated Days: Not enough data for forecasting (Average Income)"
        } else {
            binding.EstimatedDayAverage.text = "Estimated Days: $daysToReachTarget (Average Income)"
        }

        return daysToReachTarget
    }





}




