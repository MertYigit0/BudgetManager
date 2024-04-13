package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Expense
import com.mertyigit0.budgetmanager.databinding.FragmentAddExpenseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { it1 -> dbHelper.getUserData(it1) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddExpenseBinding.inflate(inflater,container,false)
        val view = binding.root;
        createToggleButtonsForIncomeCategories()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)

        setupToggleButtonGroup()
        addExpense()



    }
    private fun setupToggleButtonGroup() {
        toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val checkedButton = group.findViewById<MaterialButton>(checkedId)
            if (isChecked) {
                showSnackbar(checkedButton.text.toString())
                uncheckOtherButtons(group, checkedId)
            }
        }

    }

    private fun uncheckOtherButtons(group: MaterialButtonToggleGroup, checkedId: Int) {
        for (button in group.children) {
            if (button.id != checkedId) {
                group.uncheck(button.id)
            }
        }
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }



    private fun addExpenseToDatabase(amount: Double, category: String,categoryId : Int, date: String, description: String?,currency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val expense = Expense(id = 0, userId = userId, amount = amount, currency = currency, categoryId = categoryId, categoryName = category, date = date, note = description ?: "", createdAt = "")

        val databaseHelper = DatabaseHelper(requireContext())
        return databaseHelper.addExpense(expense)
    }

    private fun addExpense() {
        binding.addButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = getSelectedCategory()
            val date = getCurrentDate()
            val categoryId = getSelectedCategoryId()
            val description = binding.editTextText.text.toString()
            val currency = binding.currencySpinner.selectedItem.toString()

            if (addExpenseToDatabase(amount, category,categoryId, date, description, currency )) {
                showSnackbar("Expense added: $amount  $currency")
                updateBudgetAlertForCategory(categoryId)
                findNavController().navigate(R.id.action_addExpenseFragment_to_expenseFragment)
            } else {
                showSnackbar("Failed to add expense.")
            }
        }
    }






    private fun getSelectedCategory(): String {
        val selectedButtonId = toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun getSelectedCategoryId(): Int {
        val selectedCategoryName = getSelectedCategory()
        val dbHelper = DatabaseHelper(requireContext())
        val  selectedCategoryId =   dbHelper.getExpenseCategoryIdByCategoryName(selectedCategoryName)
        return selectedCategoryId
    }


    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    fun createToggleButtonsForIncomeCategories() {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val expenseCategories = dbHelper.getAllExpenseCategoriesByUserId(userId)

        for (category in expenseCategories) {
            val button = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.text = category
            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            button.setOnClickListener {
                // Buttona tıklandığında yapılacak işlemler
                showSnackbar("Clicked: $category")
            }

            // ToggleGroup'a butonları ekleme işlemi
            binding.toggleButtonGroup.addView(button)
        }
    }


    // BudgetAlert'leri güncelleme işlevini oluşturun
    private fun updateBudgetAlertForCategory(categoryId: Int) {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val totalExpense = dbHelper.getTotalExpenseForCategoryInCurrentMonth(userId, categoryId)
        val budgetAlert = dbHelper.getBudgetAlertForCategoryByUserId(userId,categoryId)

        if (budgetAlert != null) {
            // Bütçe uyarısı varsa, güncelleme yap
            val updatedBudgetAlert = budgetAlert.copy(currentAmount = totalExpense)
            updateBudgetAlert(updatedBudgetAlert)
        }
    }

    // Bütçe uyarısını güncelleme işlevini oluşturun
    private fun updateBudgetAlert(budgetAlert: BudgetAlert) {

        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.updateBudgetAlert(budgetAlert)
    }


}