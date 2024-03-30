package com.mertyigit0.budgetmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.data.Income

class FinancialGoalAdapter(private val financialGoals: List<FinancialGoal>) :
    RecyclerView.Adapter<FinancialGoalAdapter.FinancialGoalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancialGoalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_goal, parent, false)
        return FinancialGoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinancialGoalViewHolder, position: Int) {
        val currentFinancialGoal = financialGoals[position]
        holder.titleTextView.text = currentFinancialGoal.title
        holder.targetAmountTextView.text = "Target Amount: ${currentFinancialGoal.targetAmount}"
        holder.deadlineTextView.text = "Deadline: ${currentFinancialGoal.deadline}"

    }


    override fun getItemCount() = financialGoals.size

    inner class FinancialGoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val targetAmountTextView: TextView = itemView.findViewById(R.id.targetAmountTextView)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
    }




}
