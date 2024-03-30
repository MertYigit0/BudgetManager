package com.mertyigit0.budgetmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.Expense


class ExpenseAdapter(private val expenseList: ArrayList<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]

        holder.itemView.findViewById<TextView>(R.id.textViewAmount).text = expense.amount.toString()
        holder.itemView.findViewById<TextView>(R.id.textViewCurrency).text = expense.currency
        holder.itemView.findViewById<TextView>(R.id.textViewDate).text = expense.date
        holder.itemView.findViewById<TextView>(R.id.textViewCategory).text = expense.categoryName
        holder.itemView.findViewById<TextView>(R.id.textViewDescription).text =expense.note
    }

    fun updateExpenseList(newExpenseList: List<Expense>) {
        expenseList.clear()
        expenseList.addAll(newExpenseList)
        notifyDataSetChanged()
    }
}
