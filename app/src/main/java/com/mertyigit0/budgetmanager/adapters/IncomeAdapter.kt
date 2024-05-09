package com.mertyigit0.budgetmanager.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.CombinedIncome
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income

class IncomeAdapter(val context: Context, private val incomeList: ArrayList<CombinedIncome>, private val navController: NavController) :
    RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuIcon: ImageView = itemView.findViewById(R.id.menuIconIncome) // Menu iconunu tanımla
    }

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

    fun updateIncomeList(newIncomeList: List<CombinedIncome>) {
        incomeList.clear()
        incomeList.addAll(newIncomeList)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int):CombinedIncome {
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

        val isDeletedFromDatabase = if (deletedIncome.recurrence == null) {
            // Eğer recurrence değeri null ise, deleteRegularIncome fonksiyonunu çağır
            dbHelper.deleteIncome(deletedIncome.id.toLong())
        } else {
            // Eğer recurrence değeri null değilse, deleteIncome fonksiyonunu çağır
            dbHelper.deleteRegularIncome(deletedIncome.id.toLong())
        }

        if (!isDeletedFromDatabase) {

        } else {
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

