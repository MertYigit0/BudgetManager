package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Expense
import com.mertyigit0.budgetmanager.data.RecurringPayment
import com.mertyigit0.budgetmanager.databinding.FragmentEditExpenseBinding
import com.mertyigit0.budgetmanager.databinding.FragmentEditFinancialGoalBinding
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseBinding


class EditExpenseFragment : Fragment() {

    private var _binding: FragmentEditExpenseBinding? = null;
    private val binding get() = _binding!!;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditExpenseBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expenseId = arguments?.getInt("expenseId", -1)
        val regularExpenseId = arguments?.getInt("regularExpenseId", -1)
        val dbHelper = DatabaseHelper(requireContext())
        val navController = Navigation.findNavController(requireView())

        var expense: Expense? = null
        var regularExpense: RecurringPayment? = null

        if (expenseId != null && expenseId != -1) {
            expense = dbHelper.getExpenseById(expenseId)
        }

        if (regularExpenseId != null && regularExpenseId != -1) {
            regularExpense = dbHelper.getRecurringPaymentById(regularExpenseId)
        }


        if (expense != null) {
            binding.amountEditText.setText(String.format("%.2f", expense.amount))
            binding.editTextText.setText(expense.note)
            binding.dateTextView.text = expense.date

            binding.addButton.setOnClickListener {
                val updatedAmount = binding.amountEditText.text.toString().toDouble()
                val updatedDescription = binding.editTextText.text.toString()
                val updatedDate = binding.dateTextView.text.toString()

                // Güncellenmiş gideri oluştur
                val updatedExpense = Expense(
                    id = expenseId!!, // Güncellenen giderin kimliği
                    amount = updatedAmount,
                    note = updatedDescription,
                    date = updatedDate,
                    categoryId = expense.categoryId,
                    createdAt = expense.createdAt ?: "",
                    userId = expense.userId,
                    currency = expense.currency ?: "USD",
                    categoryName = expense.categoryName ?: ""
                )

                // Veritabanında güncelleme işlemi yap
                dbHelper.updateExpense(updatedExpense)

                // Giderleri gösteren fragmenta geri dön
                navController.navigate(R.id.action_editExpenseFragment_to_expenseFragment)
            }
        } else if (regularExpense != null) {
            binding.amountEditText.setText(String.format("%.2f", regularExpense.amount))
            binding.editTextText.setText(regularExpense.title)
            binding.dateTextView.text = regularExpense.nextPaymentDate

            binding.addButton.setOnClickListener {
                val updatedAmount = binding.amountEditText.text.toString().toDouble()
                val updatedTitle = binding.editTextText.text.toString()
                val updatedDate = binding.dateTextView.text.toString()

                // Güncellenmiş düzenli ödemeyi oluştur
                val updatedRegularExpense = RecurringPayment(
                    id = regularExpenseId!!, // Güncellenen düzenli ödemenin kimliği
                    userId = regularExpense.userId,
                    title = updatedTitle,
                    amount = updatedAmount,
                    currency = regularExpense.currency ?: "USD",
                    recurrence = regularExpense.recurrence ?: "",
                    nextPaymentDate = updatedDate,
                    categoryId = regularExpense.categoryId,
                    categoryName = regularExpense.categoryName ?: ""
                )

                // Veritabanında güncelleme işlemi yap
                dbHelper.updateRecurringPayment(updatedRegularExpense)

                // Giderleri gösteren fragmenta geri dön
                navController.navigate(R.id.action_editExpenseFragment_to_expenseFragment)
            }
        }

    }
}

