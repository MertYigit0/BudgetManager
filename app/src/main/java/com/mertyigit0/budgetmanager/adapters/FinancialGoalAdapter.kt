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

class FinancialGoalAdapter(private val context: Context, private val financialGoals: MutableList<FinancialGoal>, private val navController: NavController) :
    RecyclerView.Adapter<FinancialGoalAdapter.FinancialGoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialGoalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_goal, parent, false)
        return FinancialGoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinancialGoalViewHolder, position: Int) {
        val currentFinancialGoal = financialGoals[position]
        val dbHelper = DatabaseHelper(context)
        holder.bind(currentFinancialGoal)


        // Menü düğmesine tıklama dinleyicisi ekleme
        holder.menuButton.setOnClickListener {
            // PopupMenu oluşturma
            val popupMenu = PopupMenu(context, holder.menuButton)
            popupMenu.menuInflater.inflate(R.menu.item_budget_alert_menu, popupMenu.menu)

            // Menü öğelerine tıklama dinleyicisi ekleme
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Düzenleme işlemi
                        val financialGoalId = currentFinancialGoal.id
                        val bundle = Bundle().apply {
                            putInt("financialGoalId", financialGoalId)
                        }
                        ///


                        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
                        if (userData != null) {
                            val dailyIncomeList = dbHelper.getDailyIncomeForFinancialGoalById(userData.id, financialGoalId)
                            for ((date, income) in dailyIncomeList) {
                                println("Date: $date, Income: $income")
                            }

                            //
                            val incomeList = mutableListOf<Double>()

                            for ((_, income) in dailyIncomeList) {
                                incomeList.add(income)
                            }


                            dbHelper.getFinancialGoalById(financialGoalId)?.targetAmount?.let { it1 ->
                                main(incomeList,
                                    it1
                                )
                            }

                        }


                        /////
                        navController.navigate(R.id.action_financialGoalFragment_to_editFinancialGoalFragment, bundle)
                        true
                    }
                    R.id.action_delete -> {
                        // Silme işlemi
                        dbHelper.deleteFinancialGoal(currentFinancialGoal.id)
                        deleteItem(position)
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
        private val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
        private val currentAmountTextView: TextView = itemView.findViewById(R.id.currentAmountTextView)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private var image:ImageView = itemView.findViewById(R.id.imageFinancialGoal)
        val menuButton: ImageButton = itemView.findViewById(R.id.menuButton) // Menü düğmesi
        @SuppressLint("SetTextI18n")
        fun bind(financialGoal: FinancialGoal) {
            titleTextView.text = financialGoal.title
            targetAmountTextView.text = "Target Amount: ${"%.2f".format(financialGoal.targetAmount)}"
            currentAmountTextView.text = "Current Amount: ${"%.2f".format(financialGoal.currentAmount)}"
            deadlineTextView.text = "Deadline: ${financialGoal.deadline}"
            // ProgressBar'ı güncelle
            val progress = (financialGoal.currentAmount / financialGoal.targetAmount * 100).toInt()
            progressBar.progress = progress

            // Glide kullanarak görüntü yükleme
            Glide.with(itemView)
                .load(financialGoal.photo) // Görüntünün URI'sini yükle
                .placeholder(R.drawable.baseline_add_24) // Yükleme sırasında görüntülenecek yer tutucu resim
                .error(R.drawable.baseline_add_24) // Hata durumunda görüntülenecek yer tutucu resim
                .into(image) // Görüntünün gösterileceği ImageView
        }
    }

    fun deleteItem(position: Int) {
        financialGoals.removeAt(position)
        notifyItemRemoved(position)
    }

    fun main(dailyIncomes: List<Double>, targetAmount: Double) {


        // Kullanıcıların günlere göre gelirlerini içeren bir liste
        //val dailyIncomes = da

        // Toplam biriktirilmek istenen değer
        //val targetAmount = 10000.0

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
