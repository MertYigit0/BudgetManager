import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.R

class BudgetAlertAdapter(private val context: Context, private val budgetAlertList: List<BudgetAlert>) :
    RecyclerView.Adapter<BudgetAlertAdapter.BudgetAlertViewHolder>() {

    inner class BudgetAlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val limitAmountTextView: TextView = itemView.findViewById(R.id.limitAmountTextView)
        val currentAmountTextView: TextView = itemView.findViewById(R.id.currentAmountTextView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
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

        holder.categoryTextView.text = budgetAlert.categoryName
        holder.limitAmountTextView.text = "Target Amount: ${budgetAlert.targetAmount}"
        holder.currentAmountTextView.text = "Current Amount: ${budgetAlert.currentAmount}"

        val progress = (budgetAlert.currentAmount.toFloat() / budgetAlert.targetAmount.toFloat() * 100).toInt()
        holder.progressBar.progress = progress
    }
}
