package com.mertyigit0.budgetmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.io.Serializable

class FinancialGoalAdapter(private val context: Context, private val financialGoals: MutableList<FinancialGoal>, private val navController: NavController) :
    RecyclerView.Adapter<FinancialGoalAdapter.FinancialGoalViewHolder>() {

    private val dbHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialGoalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_goal, parent, false)
        return FinancialGoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinancialGoalViewHolder, position: Int) {
        val currentFinancialGoal = financialGoals[position]
        holder.bind(currentFinancialGoal)

        // Menü düğmesine tıklama dinleyicisi ekleme
        holder.menuButton.setOnClickListener {
            // PopupMenu oluşturma
            val popupMenu = PopupMenu(context, holder.menuButton)
            popupMenu.menuInflater.inflate(R.menu.item_financial_goal_menu, popupMenu.menu)

            // Menü öğelerine tıklama dinleyicisi ekleme
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Düzenleme işlemi
                        val financialGoalId = currentFinancialGoal.id
                        val bundle = Bundle().apply {
                            putInt("financialGoalId", financialGoalId)
                        }

                        navController.navigate(R.id.action_financialGoalFragment_to_editFinancialGoalFragment, bundle)
                        true
                    }
                    R.id.action_delete -> {
                        // Silme işlemi
                        dbHelper.deleteFinancialGoal(currentFinancialGoal.id)
                        deleteItem(position)
                        true
                    }
                    R.id.action_detail -> {
                        val bundle1 = Bundle().apply {
                            putInt("financialGoalId", currentFinancialGoal.id)
                        }
                        navController.navigate(R.id.action_financialGoalFragment_to_financialGoalDetailFragment, bundle1)
                        true
                    }
                    else -> false
                }
            }

            // Popup menüyü gösterme
            popupMenu.show()
        }
    }

    override fun getItemCount() = financialGoals.size

    inner class FinancialGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val targetAmountTextView: TextView = itemView.findViewById(R.id.targetAmountTextView)
        private val predictTextView: TextView = itemView.findViewById(R.id.predictTextView)
        private val currentAmountTextView: TextView = itemView.findViewById(R.id.currentAmountTextView)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private var image: ImageView = itemView.findViewById(R.id.imageFinancialGoal)
         val menuButton: ImageButton = itemView.findViewById(R.id.menuButton) // Menü düğmesi


        @SuppressLint("SetTextI18n")
        fun bind(financialGoal: FinancialGoal) {
            titleTextView.text = financialGoal.title
            targetAmountTextView.text = "Target Amount: ${"%.2f".format(financialGoal.targetAmount)}"
            currentAmountTextView.text = "Current Amount: ${"%.2f".format(financialGoal.currentAmount)}"
            // ProgressBar'ı güncelle
            val progress = (financialGoal.currentAmount / financialGoal.targetAmount * 100).toInt()
            progressBar.progress = progress

            // Glide kullanarak görüntü yükleme
            Glide.with(itemView)
                .load(financialGoal.photo)
                .placeholder(R.drawable.baseline_add_24)
                .error(R.drawable.baseline_add_24)
                .into(image)

            // Tahmini gün sayısını güncelle
            updatePredictedDays(financialGoal)
        }

        private fun updatePredictedDays(financialGoal: FinancialGoal) {
            val financialGoalId = financialGoal.id
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
            if (userData != null) {
                val dailyIncomeList = dbHelper.getCombinedDailyAndRegularIncomeForFinancialGoalById(userData.id, financialGoalId)

                println("Daily Income List: $dailyIncomeList")

                if (dailyIncomeList.isEmpty()) {
                    predictTextView.text = "No income data available"
                    return
                }

                val incomeList = mutableListOf<Double>()

                for ((_, income) in dailyIncomeList) {
                    incomeList.add(income)
                }

                println("Income List: $incomeList")

                if (incomeList.isEmpty() || incomeList.sum() == 0.0) {
                    predictTextView.text = "No income available or total income is zero"
                    return
                }

                if (incomeList.size < 10) {
                    predictTextView.text = "Not enough data for prediction"
                } else {
                    val targetAmount = financialGoal.targetAmount
                    val futureDays = linearRegressionForecast(incomeList, targetAmount)
                    if (futureDays == -1) {
                        predictTextView.text = "Uncertain prediction due to decreasing income trend"
                    } else if (futureDays == -2) {
                        predictTextView.text = "Not enough data for prediction"
                    } else {
                        val targetAmount = financialGoal.targetAmount
                        val currentAmount = financialGoal.currentAmount
                        if (futureDays <= 0 || currentAmount >= targetAmount) {

                            predictTextView.text = "Target amount reached!"

                        } else {
                            predictTextView.text= "Approximately Days: $futureDays"
                        }
                    }
                }
            }
        }


    }

    fun deleteItem(position: Int) {
        financialGoals.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun linearRegressionForecast(dailyIncomes: List<Double>, targetAmount: Double): Int {

        val regression = SimpleRegression()

        // Gelir verileri yeterli mi kontrol et
        if (dailyIncomes.size < 10) {
            println("aaaaaaaaaaa"+regression.slope) 
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

}
