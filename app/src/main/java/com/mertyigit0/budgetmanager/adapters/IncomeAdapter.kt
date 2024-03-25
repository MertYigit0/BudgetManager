package com.mertyigit0.budgetmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.Income

class IncomeAdapter(private val incomeList: ArrayList<Income>) :
    RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_income, parent, false)
        return IncomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return incomeList.size
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomeList[position]

        holder.itemView.findViewById<TextView>(R.id.textViewAmount).text = income.amount.toString()
        holder.itemView.findViewById<TextView>(R.id.textViewCurrency).text = income.currency
        holder.itemView.findViewById<TextView>(R.id.textViewDate).text = income.date
    }

    fun updateIncomeList(newIncomeList: List<Income>) {
        incomeList.clear()
        incomeList.addAll(newIncomeList)
        notifyDataSetChanged()
    }
}
