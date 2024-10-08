package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Expense
import com.mertyigit0.budgetmanager.data.RecurringPayment
import com.mertyigit0.budgetmanager.databinding.FragmentAddExpenseBinding
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddExpenseFragment : Fragment() {
    private var _binding: FragmentAddExpenseBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup

    private var selectedDate: String? = null

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


        var dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        var userCurrency = userData?.currency
        userCurrency?.let { currency ->
            val currencyIndex = getIndexOfCurrencyInSpinner(currency)
            binding.currencySpinner.setSelection(currencyIndex)
        }

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)

        setupToggleButtonGroup()

        binding.regularExpenseCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                addRecurringPayment()
                binding.regularExpenseSpinner.visibility = View.VISIBLE
                binding.titleRegularExpenseEditText.visibility = View.VISIBLE

            }else{
                binding.regularExpenseSpinner.visibility = View.GONE
                binding.titleRegularExpenseEditText.visibility = View.GONE
            }
        }


        addExpense()


        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.addCategoryButton.setOnClickListener{
            addNewExpenseCategory()

        }



    }
    private fun clearToggleButtons() {
        binding.toggleButtonGroup.removeAllViews()
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



    private fun addExpenseToDatabase(amount: Double, category: String, categoryId: Int, date: String, description: String?, currency: String, userCurrency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // İlk olarak, gelen miktarı dolar cinsine dönüştür
        val amountInUSD = if (currency == "USD") {
            amount
        } else {
            val exchangeRateToUSD = dbHelper.getExchangeRate(currency)
            amount / exchangeRateToUSD
        }

        // Dönüştürülen miktarı kullanıcının seçtiği para birimine dönüştür
        val convertedAmount = if (userCurrency == "USD") {
            amountInUSD
        } else {
            val exchangeRateToUserCurrency = dbHelper.getExchangeRate(userCurrency)
            amountInUSD * exchangeRateToUserCurrency
        }


        val formattedAmount = String.format(Locale.ENGLISH, "%.2f", convertedAmount)

        val amountAsDouble = formattedAmount.toDouble()

        val equivalentAmountText = "$formattedAmount $userCurrency"

        val expense = Expense(id = 0, userId = userId, amount = amountAsDouble, currency = userCurrency, categoryId = categoryId, categoryName = category, date = date, note = description ?: "", createdAt = "")

        val databaseHelper = DatabaseHelper(requireContext())

        val isSuccess = databaseHelper.addExpense(expense)


        if (isSuccess) {

            showSnackbar("Expense added: $amount $currency (Equivalent: $equivalentAmountText)")

            updateBudgetAlertForCategory(categoryId)
        } else {

            showSnackbar("Failed to add expense.")
        }
        return isSuccess
    }



    private fun addExpense() {
        binding.addButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = getSelectedCategory()
            val date = selectedDate ?: getCurrentDate()
            val categoryId = getSelectedCategoryId()
            val description = binding.editTextText.text.toString()
            val currency = binding.currencySpinner.selectedItem.toString()
            val userCurrency = getUserCurrency()

            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }
            if (amount.equals(0.0)) {
                showSnackbar("Please enter an amount .")
                return@setOnClickListener
            }

            if (addExpenseToDatabase(amount, category,categoryId, date, description, currency,userCurrency )) {
                findNavController().navigate(R.id.action_addExpenseFragment_to_expenseFragment)
                showSnackbar("Expense added: $amount  $currency")
                updateBudgetAlertForCategory(categoryId)

            } else {
                showSnackbar("Failed to add expense.")
            }
        }
    }


    private fun addRecurringPaymentToDatabase(title: String, amount: Double, currency: String, recurrence: String, nextPaymentDate: String, categoryId: Int, category: String, userCurrency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // İlk olarak, gelen miktarı dolar cinsine dönüştür
        val amountInUSD = if (currency == "USD") {
            amount
        } else {
            val exchangeRateToUSD = dbHelper.getExchangeRate(currency)
            amount / exchangeRateToUSD
        }

        // Dönüştürülen miktarı kullanıcının seçtiği para birimine dönüştür
        val convertedAmount = if (userCurrency == "USD") {
            amountInUSD
        } else {
            val exchangeRateToUserCurrency = dbHelper.getExchangeRate(userCurrency)
            amountInUSD * exchangeRateToUserCurrency
        }


        val formattedAmount = String.format(Locale.ENGLISH, "%.2f", convertedAmount)

        val amountAsDouble = formattedAmount.toDouble()

        val equivalentAmountText = "$formattedAmount $userCurrency"

        val recurringPayment = RecurringPayment(
            id = 0,
            userId = userId,
            title = title,
            amount = amountAsDouble,
            currency = userCurrency,
            recurrence = recurrence,
            nextPaymentDate = nextPaymentDate,
            categoryId = categoryId,
            categoryName = category
        )

        val databaseHelper = DatabaseHelper(requireContext())

        val isSuccess = databaseHelper.addRecurringPayment(recurringPayment)


        if (isSuccess) {

            showSnackbar("Recurring payment added: $amount $currency (Equivalent: $equivalentAmountText)")
        } else {

            showSnackbar("Failed to add recurring payment.")
        }
        return isSuccess
    }

    private fun addRecurringPayment() {
        binding.addButton.setOnClickListener {
            val title = binding.titleRegularExpenseEditText.text.toString()
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val currency = binding.currencySpinner.selectedItem.toString()
            val recurrence = binding.regularExpenseSpinner.selectedItem.toString()
            val nextPaymentDate = selectedDate ?: getCurrentDate()
            val categoryId = getSelectedCategoryId()
            val userCurrency = getUserCurrency()
            val category = getSelectedCategory()
            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }
            if (amount.equals(0.0)) {
                showSnackbar("Please enter an amount.")
                return@setOnClickListener
            }

            if (addRecurringPaymentToDatabase(title, amount, currency, recurrence, nextPaymentDate, categoryId,category,userCurrency)) {
                findNavController().navigate(R.id.action_addExpenseFragment_to_expenseFragment)
                showSnackbar("Recurring payment added: $amount $currency")
            } else {
                showSnackbar("Failed to add recurring payment.")
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

                showSnackbar("Clicked: $category")
            }


            // ToggleGroup'a butonları ekle
            binding.toggleButtonGroup.addView(button)
        }
    }






    // BudgetAlert'leri güncelle
    private fun updateBudgetAlertForCategory(categoryId: Int) {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val totalExpense = dbHelper.getTotalExpenseForCategoryInCurrentMonth(userId, categoryId)
        val budgetAlert = dbHelper.getBudgetAlertForCategoryByUserId(userId,categoryId)

        if (budgetAlert != null) {
            // Bütçe uyarısı varsa güncelleme yap
            val updatedBudgetAlert = budgetAlert.copy(currentAmount = totalExpense)
            updateBudgetAlert(updatedBudgetAlert)

        }
    }


    private fun updateBudgetAlert(budgetAlert: BudgetAlert) {

        val dbHelper = DatabaseHelper(requireContext())
        dbHelper.updateBudgetAlert(budgetAlert)
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                binding.dateTextView.text = selectedDate // Seçilen tarihi bir TextView'a yazdırın
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    fun addNewExpenseCategory() {
        val dbHelper = DatabaseHelper(requireContext())
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Add New Category")
        val input = EditText(requireContext())
        input.hint = "Category Name"
        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { dialog, which ->
            val categoryName = input.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
                val userId = userData?.id
                val categoryId = userId?.let { dbHelper.addExpenseCategory(it, categoryName) }
                if (categoryId != -1L) {

                    Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                    // ToggleButton grubunu temizle ve yeniden oluştur
                    clearToggleButtons()
                    createToggleButtonsForIncomeCategories()
                } else {

                    Toast.makeText(requireContext(), "Failed to add category", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Kategori adı boş
                Toast.makeText(requireContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        alertDialog.show()
    }


    private fun getIndexOfCurrencyInSpinner(currency: String): Int {
        val spinnerAdapter = binding.currencySpinner.adapter
        val count = spinnerAdapter.count
        for (i in 0 until count) {
            if (spinnerAdapter.getItem(i).toString() == currency) {
                return i
            }
        }
        return 0 // Varsayılan olarak 0. indeksi döndür
    }

    private fun getUserCurrency(): String {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        return currentUserEmail?.let { dbHelper.getUserData(it)?.currency } ?: "USD" // Varsayılan olarak USD kullan
    }



}