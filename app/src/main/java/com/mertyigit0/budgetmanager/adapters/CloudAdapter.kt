package com.mertyigit0.budgetmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.CloudItem

class CloudAdapter(private val items: List<CloudItem>) : RecyclerView.Adapter<CloudAdapter.CloudViewHolder>() {

    class CloudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CloudViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cloud, parent, false)
        return CloudViewHolder(view)
    }

    override fun onBindViewHolder(holder: CloudViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.descriptionTextView.text = item.description
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
