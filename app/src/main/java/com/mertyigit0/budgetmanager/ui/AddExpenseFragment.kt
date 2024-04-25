package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)

        setupToggleButtonGroup()
        addExpense()


        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }



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



    private fun addExpenseToDatabase(amount: Double, category: String, categoryId: Int, date: String, description: String?, currency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // Döviz kuru veritabanından al
        val exchangeRate = dbHelper.getExchangeRate(currency)

        // Dönüştürülmüş miktarı hesapla ve en fazla 2 basamakla sınırla
        val convertedAmount = (amount / exchangeRate).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        // Dönüştürülmüş miktarı, para birimiyle birlikte ekranda göstermek için string oluştur
        val equivalentAmountText = "%.2f".format(convertedAmount) + " USD"

        // Expense nesnesini oluştur
        val expense = Expense(id = 0, userId = userId, amount = convertedAmount, currency = "USD", categoryId = categoryId, categoryName = category, date = date, note = description ?: "", createdAt = "")

        // Expense'ı veritabanına ekle
        val databaseHelper = DatabaseHelper(requireContext())
        val isSuccess = databaseHelper.addExpense(expense)

        // Eğer eklenme başarılı ise
        if (isSuccess) {
            // Snackbar'da dönüştürülmüş miktarı ve para birimini göster
            showSnackbar("Expense added: $amount $currency (Equivalent: $equivalentAmountText)")
            // Bütçe uyarısını güncelle
            updateBudgetAlertForCategory(categoryId)

        } else {
            // Ekleme başarısız olduysa Snackbar göster
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


            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }
            if (amount.equals(0.0)) {
                showSnackbar("Please enter an amount .")
                return@setOnClickListener
            }

            if (addExpenseToDatabase(amount, category,categoryId, date, description, currency )) {
                findNavController().navigate(R.id.action_addExpenseFragment_to_expenseFragment)
                showSnackbar("Expense added: $amount  $currency")
                updateBudgetAlertForCategory(categoryId)

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


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Tarihi doğru formatta ayarlayın
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


}