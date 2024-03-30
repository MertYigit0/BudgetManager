package com.mertyigit0.budgetmanager.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.FinancialGoal

class FinancialGoalAdapter(private val financialGoals: List<FinancialGoal>) :
    RecyclerView.Adapter<FinancialGoalAdapter.FinancialGoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialGoalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_goal, parent, false)
        return FinancialGoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinancialGoalViewHolder, position: Int) {
        val currentFinancialGoal = financialGoals[position]
        holder.bind(currentFinancialGoal)
    }

    override fun getItemCount() = financialGoals.size

    inner class FinancialGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val targetAmountTextView: TextView = itemView.findViewById(R.id.targetAmountTextView)
        private val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)

        @SuppressLint("SetTextI18n")
        fun bind(financialGoal: FinancialGoal) {
            titleTextView.text = financialGoal.title
            targetAmountTextView.text = "Target Amount: ${financialGoal.targetAmount}"
            deadlineTextView.text = "Deadline: ${financialGoal.deadline}"
        }
    }
}

