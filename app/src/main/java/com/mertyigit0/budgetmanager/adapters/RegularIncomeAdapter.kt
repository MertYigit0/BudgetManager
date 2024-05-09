package com.mertyigit0.budgetmanager.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.CombinedIncome
import com.mertyigit0.budgetmanager.data.RegularIncome

class RegularIncomeAdapter(val context: Context, private val regularIncomeList: ArrayList<RegularIncome>, private val navController: NavController) :
    RecyclerView.Adapter<RegularIncomeAdapter.RegularIncomeViewHolder>() {

    class RegularIncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuIcon: ImageView = itemView.findViewById(R.id.menuIconIncome) // Menu iconunu tanımla
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegularIncomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_income, parent, false)
        return RegularIncomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return regularIncomeList.size
    }

    override fun onBindViewHolder(holder: RegularIncomeViewHolder, position: Int) {
        val income = regularIncomeList[position]

        val formattedAmount = String.format("%.2f", income.amount)
        holder.itemView.findViewById<TextView>(R.id.textViewAmount).text = "$formattedAmount "
        holder.itemView.findViewById<TextView>(R.id.textViewCurrency).text = income.currency
        holder.itemView.findViewById<TextView>(R.id.textViewDate).text = income.date
        holder.itemView.findViewById<TextView>(R.id.textViewCategory).text = income.categoryName
        holder.itemView.findViewById<ImageView>(R.id.menuIconIncome)
        // holder.itemView.findViewById<TextView>(R.id.textViewDescription).text = income.note



        // Menü düğmesine tıklama dinleyicisi ekleme
        holder.itemView.findViewById<ImageView>(R.id.menuIconIncome).setOnClickListener {
            // PopupMenu oluşturma
            val popupMenu = PopupMenu(context, holder.itemView.findViewById(R.id.menuIconIncome))
            popupMenu.inflate(R.menu.item_income_menu)

            // Menü öğelerine tıklama dinleyicisi ekleme
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Düzenleme işlemi
                        val incomeId = income.id
                        val bundle = Bundle().apply {
                            putInt("incomeId", incomeId)
                        }
                        navController.navigate(R.id.action_incomeFragment_to_editIncomeFragment, bundle)
                        true
                    }
                    else -> false
                }
            }

            // Popup menüyü gösterme
            popupMenu.show()
        }








    }

    fun updateIncomeList(newRegularIncomeList: List<RegularIncome>) {
        regularIncomeList.clear()
        regularIncomeList.addAll(newRegularIncomeList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int): RegularIncome {
        val deletedRegularIncome = regularIncomeList.removeAt(position)
        notifyItemRemoved(position)
        return deletedRegularIncome
    }


}