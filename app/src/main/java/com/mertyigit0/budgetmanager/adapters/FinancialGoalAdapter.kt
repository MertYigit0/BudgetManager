package com.mertyigit0.budgetmanager.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal

class FinancialGoalAdapter(private val context: Context, private val financialGoals: MutableList<FinancialGoal>) :
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
                        // TODO: Düzenleme işlemini burada gerçekleştir
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
        val menuButton: ImageButton = itemView.findViewById(R.id.menuButton) // Menü düğmesi
        @SuppressLint("SetTextI18n")
        fun bind(financialGoal: FinancialGoal) {
            titleTextView.text = financialGoal.title
            targetAmountTextView.text = "Target Amount: ${financialGoal.targetAmount}"
            deadlineTextView.text = "Deadline: ${financialGoal.deadline}"
            currentAmountTextView.text ="Current Amouint : ${financialGoal.currentAmount}"
            // ProgressBar'ı güncelle
            val progress = (financialGoal.currentAmount / financialGoal.targetAmount * 100).toInt()
            progressBar.progress = progress
        }
    }

    fun deleteItem(position: Int) {
        financialGoals.removeAt(position)
        notifyItemRemoved(position)
    }
}
