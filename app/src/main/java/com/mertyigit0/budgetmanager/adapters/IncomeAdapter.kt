package com.mertyigit0.budgetmanager.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(adapter.context, R.drawable.baseline_delete_forever_24)
    private val background: ColorDrawable = ColorDrawable(Color.RED)

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
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
        val iconBottom = iconTop + deleteIcon.intrinsicHeight

        if (dX > 0) { // Sağa kaydırma
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + deleteIcon.intrinsicWidth
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        } else if (dX < 0) { // Sola kaydırma
            val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

        background.draw(c)
        deleteIcon.draw(c)
    }
}

