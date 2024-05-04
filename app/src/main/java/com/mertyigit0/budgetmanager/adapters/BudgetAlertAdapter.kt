import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income

class BudgetAlertAdapter(private val context: Context, private val budgetAlertList: ArrayList<BudgetAlert>, private val navController: NavController) :
    RecyclerView.Adapter<BudgetAlertAdapter.BudgetAlertViewHolder>() {

    inner class BudgetAlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val limitAmountTextView: TextView = itemView.findViewById(R.id.limitAmountTextView)
        val currentAmountTextView: TextView = itemView.findViewById(R.id.currentAmountTextView)
        val limitText : TextView = itemView.findViewById(R.id.limitText)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val menuButton: ImageButton = itemView.findViewById(R.id.menuButton) // Menü düğmesi
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetAlertViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_budget_alert, parent, false)

        return BudgetAlertViewHolder(view)
    }

    override fun getItemCount(): Int {
        return budgetAlertList.size
    }


    override fun onBindViewHolder(holder: BudgetAlertViewHolder, position: Int) {
        val budgetAlert = budgetAlertList[position]
        val dbHelper = DatabaseHelper(context)
        val categoryName = budgetAlert.categoryId?.let {
            dbHelper.getExpenseCategoryNameByCategoryId(
                it
            )
        }

        holder.categoryTextView.text = categoryName
        holder.limitAmountTextView.text = "Target Amount: ${"%.2f".format(budgetAlert.targetAmount)}"
        holder.currentAmountTextView.text = "Current Amount: ${"%.2f".format(budgetAlert.currentAmount)}"

        // Kalan limitin gösterilmesi ve drawable eklenmesi
        val remainingAmount = budgetAlert.targetAmount - budgetAlert.currentAmount
        val drawable = when {
            remainingAmount < 0 -> ContextCompat.getDrawable(context, R.drawable.baseline_mood_bad_24)
            remainingAmount < budgetAlert.targetAmount / 2 -> ContextCompat.getDrawable(context, R.drawable.baseline_mood_24)
            else -> ContextCompat.getDrawable(context, R.drawable.baseline_insert_emoticon_24)
        }

        val text = if (remainingAmount < 0) {
            "Limit Exceeded: ${-remainingAmount}" // Eğer limit aşıldıysa, negatif kısmını al
        } else {
            "Remaining Limit: $remainingAmount"
        }

        holder.limitText.text = text
        holder.limitText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)


        val progress = (budgetAlert.currentAmount.toFloat() / budgetAlert.targetAmount.toFloat() * 100).toInt()
        holder.progressBar.progress = progress
        // Progress bar rengini belirleme
        when {
            progress > 100 -> holder.progressBar.progressDrawable.setTint(Color.RED)
            progress > 50 -> holder.progressBar.progressDrawable.setTint(Color.YELLOW)
            else -> holder.progressBar.progressDrawable.setTint(Color.GREEN)
        }


        // Menü düğmesine tıklama dinleyicisi ekleme
        holder.menuButton.setOnClickListener {
            // PopupMenu oluşturma
            val popupMenu = PopupMenu(context, holder.menuButton)
            popupMenu.menuInflater.inflate(R.menu.item_budget_alert_menu, popupMenu.menu)

            // Menü öğelerine tıklama dinleyicisi ekleme
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        // Düzenleme işlemi
                        val bundle = Bundle().apply {
                            putInt("budgetAlertId", budgetAlert.id) // Bütçe uyarısı kimliğini ekleyin
                        }
                        navController.navigate(R.id.action_budgetAlertFragment_to_editBudgetAlertFragment, bundle)
                        true
                    }
                    R.id.action_delete -> {
                        // Silme işlemi
                        dbHelper.deleteBudgetAlert(budgetAlertId = budgetAlert.id)

                        deleteItem(position)


                        true
                    }

                    else -> false
                }
            }

            // Popup menüyü gösterme
            popupMenu.show()
        }



    }
    fun deleteItem(position: Int) {
        budgetAlertList.removeAt(position)
        notifyItemRemoved(position)
    }


    fun updateBudgetAlertList(newBudgetAlertList: List<BudgetAlert>) {
        budgetAlertList.clear()
        budgetAlertList.addAll(newBudgetAlertList)
        notifyDataSetChanged()
    }




}
