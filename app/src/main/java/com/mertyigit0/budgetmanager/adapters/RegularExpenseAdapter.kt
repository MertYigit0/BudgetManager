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
import com.mertyigit0.budgetmanager.data.RecurringPayment

class RegularExpenseAdapter(val context: Context, private val regularExpenseList: ArrayList<RecurringPayment>, private val navController: NavController) :
    RecyclerView.Adapter<RegularExpenseAdapter.RegularExpenseViewHolder>() {

    class RegularExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuIcon: ImageView = itemView.findViewById(R.id.menuIconIncome) // Menu iconunu tanımla
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegularExpenseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_income, parent, false)
        return RegularExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return regularExpenseList.size
    }

    override fun onBindViewHolder(holder: RegularExpenseViewHolder, position: Int) {
        val expense = regularExpenseList[position]

        val formattedAmount = String.format("%.2f", expense.amount)
        holder.itemView.findViewById<TextView>(R.id.textViewAmount).text = "$formattedAmount "
        holder.itemView.findViewById<TextView>(R.id.textViewCurrency).text = expense.currency
        holder.itemView.findViewById<TextView>(R.id.textViewDate).text = expense.nextPaymentDate
        holder.itemView.findViewById<TextView>(R.id.textViewCategory).text = expense.title
        holder.itemView.findViewById<ImageView>(R.id.menuIconIncome)
        // holder.itemView.findViewById<TextView>(R.id.textViewDescription).text = income.note

        holder.itemView.findViewById<ImageView>(R.id.menuIconIncome).setOnClickListener {
            val popupMenu = PopupMenu(context, holder.itemView.findViewById(R.id.menuIconIncome))
            popupMenu.inflate(R.menu.item_income_menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {

                        val regularExpenseId = expense.id
                        val bundle = Bundle().apply {
                            putInt("regularExpenseId", regularExpenseId)
                        }
                        navController.navigate(R.id.action_regularTransactionsFragment_to_editExpenseFragment, bundle)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }

    }

    fun updateExpenseList(newRegularExpenseList: List<RecurringPayment>) {
        regularExpenseList.clear()
        regularExpenseList.addAll(newRegularExpenseList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int): RecurringPayment {
        val deletedRegularExpense = regularExpenseList.removeAt(position)
        notifyItemRemoved(position)
        return deletedRegularExpense
    }


}