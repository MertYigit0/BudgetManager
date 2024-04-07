package com.mertyigit0.budgetmanager.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R

class CurrencyListAdapter(private val currencyList: Map<String, Double>) :
    RecyclerView.Adapter<CurrencyListAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currencyEntries = currencyList.entries.toList()
        val currencyEntry = currencyEntries[position]
        holder.currencyTextView.text = "${currencyEntry.key}: ${currencyEntry.value}"
    }

    override fun getItemCount() = currencyList.size

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyTextView: TextView = itemView.findViewById(R.id.currencyTextView)
    }
}
