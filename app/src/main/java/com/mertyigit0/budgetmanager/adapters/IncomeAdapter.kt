package com.mertyigit0.budgetmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income

class IncomeAdapter(val context: Context, private val incomeList: ArrayList<Income>) :
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
        holder.itemView.findViewById<TextView>(R.id.textViewCategory).text = income.categoryName
        holder.itemView.findViewById<TextView>(R.id.textViewDescription).text = income.note
    }

    fun updateIncomeList(newIncomeList: List<Income>) {
        incomeList.clear()
        incomeList.addAll(newIncomeList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int): Income {
        val deletedIncome = incomeList.removeAt(position)
        notifyItemRemoved(position)
        return deletedIncome
    }


}
// IncomeSwipeToDeleteCallback sınıfını tanımlayın
class IncomeSwipeToDeleteCallback(private val adapter: IncomeAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val deletedIncome = adapter.deleteItem(position) // Adapter ile ilişkilendirilmiş IncomeAdapter sınıfından deleteItem fonksiyonunu çağırın
        val dbHelper = DatabaseHelper(adapter.context) // Veritabanı işlemleri için gerekli olan context adapter'dan alınmalıdır.
        val isDeletedFromDatabase = dbHelper.deleteIncome(deletedIncome.id.toLong())
        if (!isDeletedFromDatabase) {
            // SQLite'dan silme işlemi başarısız oldu, geri almayı düşünebilirsiniz
        }else {
            // Silme işlemi başarılı oldu, PieChart'ı güncelle

        }
    }

}