package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.view.children
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income
import com.mertyigit0.budgetmanager.data.RegularIncome
import com.mertyigit0.budgetmanager.databinding.FragmentAddIncomeBinding
import com.mertyigit0.budgetmanager.util.AlarmScheduler
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random


class AddIncomeFragment : Fragment() {

    private var _binding: FragmentAddIncomeBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    private lateinit var auth: FirebaseAuth

    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            auth = FirebaseAuth.getInstance()
            val dbHelper = DatabaseHelper(requireContext())
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            val userId = currentUserEmail?.let { it1 -> dbHelper.getUserData(it1) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddIncomeBinding.inflate(inflater,container,false)
        val view = binding.root;
        createToggleButtonsForIncomeCategories()
        // FirebaseAuth nesnesini başlat
        auth = FirebaseAuth.getInstance()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  scheduleAutomaticIncomes()

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



        binding.regularIncomeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                addRegularIncome()
                binding.regularIncomeSpinner.visibility = View.VISIBLE
                binding.titleRegularIncomeEditText.visibility = View.VISIBLE
            }else{
                binding.regularIncomeSpinner.visibility = View.GONE
                binding.titleRegularIncomeEditText.visibility = View.GONE
            }
        }
        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.addCategoryButton.setOnClickListener{
            addNewIncomeCategory()

        }

        addIncome()


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




    private fun addIncomeToDatabase(amount: Double, category: String, categoryId: Int, date: String, description: String?, currency: String, userCurrency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // İlk olarak, gelen miktarı dolar cinsine dönüştür
        val amountInUSD = if (currency == "USD") {
            amount // Eğer para birimi zaten USD ise dönüştürme işlemi yapma
        } else {
            val exchangeRateToUSD = dbHelper.getExchangeRate(currency)
            amount / exchangeRateToUSD
        }

        // Şimdi, dönüştürülen miktarı kullanıcının seçtiği para birimine dönüştür
        val convertedAmount = if (userCurrency == "USD") {
            amountInUSD // Eğer kullanıcı para birimi USD ise dönüştürme işlemi yapma
        } else {
            val exchangeRateToUserCurrency = dbHelper.getExchangeRate(userCurrency)
            amountInUSD * exchangeRateToUserCurrency
        }

        // Dönüştürülen miktarı en fazla iki basamaklı bir string olarak biçimlendir
        val formattedAmount = String.format(Locale.ENGLISH, "%.2f", convertedAmount)

        val amountAsDouble = formattedAmount.toDouble()

        // Dönüştürülen miktar ve para birimini ekranda göstermek için string oluştur
        val equivalentAmountText = "$formattedAmount $userCurrency"

        val income = Income(id = 0, userId = userId, amount = amountAsDouble, currency = userCurrency, categoryId = categoryId, categoryName = category, date = date, note = description ?: "", createdAt = "")

        val databaseHelper = DatabaseHelper(requireContext())

        val isSuccess = databaseHelper.addIncome(income)

        // Eğer eklenme başarılı ise
        if (isSuccess) {
            // Snackbar'da dönüştürülmüş miktarı ve kullanıcı para birimini göster
            showSnackbar("Income added: $amount $currency (Equivalent: $equivalentAmountText)")
        } else {
            // Ekleme başarısız olduysa Snackbar göster
            showSnackbar("Failed to add income.")
        }
        return isSuccess
    }



    private fun addIncome() {
        val dbHelper = DatabaseHelper(requireContext())
        binding.addButton.setOnClickListener {
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = getSelectedCategory()
            val categoryId = getSelectedCategoryId()
            val date = selectedDate ?: getCurrentDate()
            val description = binding.editTextText.text.toString()
            val currency = binding.currencySpinner.selectedItem.toString()
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            val userCurrency = getUserCurrency()
            val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }

            // Gelirin eklendiği tarihte aynı kategoride bir finansal hedef var mı kontrol et
            val financialGoals = dbHelper.getAllFinancialGoalsByUserIdByCategoryId(userId ,  categoryId)
            financialGoals.forEach { goal ->
                if (goal.categoryId == categoryId) {
                    val updatedAmount = amount * (goal.percentage.toDouble() / 100) // Gelir miktarını yüzdeyle çarp
                    goal.currentAmount += updatedAmount
                    dbHelper.updateFinancialGoal(goal)
                }
            }

            if (addIncomeToDatabase(amount, category,categoryId, date, description,currency,userCurrency)) {
                showSnackbar("Income added: $amount $currency")
                findNavController().navigate(R.id.action_addIncomeFragment_to_incomeFragment)
            } else {
                showSnackbar("Failed to add income.")
            }
        }
    }



    private fun getUserCurrency(): String {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        return currentUserEmail?.let { dbHelper.getUserData(it)?.currency } ?: "USD" // Varsayılan olarak USD kullan
    }
    private fun addRegularIncomeToDatabase(title: String, amount: Double, currency: String, recurrence: String, date: String, categoryId: Int, category: String, userCurrency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // İlk olarak, gelen miktarı dolar cinsine dönüştür
        val amountInUSD = if (currency == "USD") {
            amount // Eğer para birimi zaten USD ise dönüştürme işlemi yapma
        } else {
            val exchangeRateToUSD = dbHelper.getExchangeRate(currency)
            amount / exchangeRateToUSD
        }

        // Şimdi, dönüştürülen miktarı kullanıcının seçtiği para birimine dönüştür
        val convertedAmount = if (userCurrency == "USD") {
            amountInUSD // Eğer kullanıcı para birimi USD ise dönüştürme işlemi yapma
        } else {
            val exchangeRateToUserCurrency = dbHelper.getExchangeRate(userCurrency)
            amountInUSD * exchangeRateToUserCurrency
        }


        val formattedAmount = String.format(Locale.ENGLISH, "%.2f", convertedAmount)

        val amountAsDouble = formattedAmount.toDouble()


        val equivalentAmountText = "$formattedAmount $userCurrency"

        val regularIncome = RegularIncome(
            id = 0,
            userId = userId,
            title = title,
            amount = amountAsDouble,
            currency = userCurrency,
            recurrence = recurrence,
            date = date,
            categoryId = categoryId,
            categoryName = category
        )

        return dbHelper.addRegularIncome(regularIncome)
    }


    private fun addRegularIncome() {
        val dbHelper = DatabaseHelper(requireContext())
        binding.addButton.setOnClickListener {
            val title = binding.titleRegularIncomeEditText.text.toString()
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val currency = binding.currencySpinner.selectedItem.toString()
            val recurrence = binding.regularIncomeSpinner.selectedItem.toString()
            val date = getCurrentDate()
            val category = getSelectedCategory()
            val categoryId = getSelectedCategoryId()
            val userCurrency = getUserCurrency()
            val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
            val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }


            // Gelirin eklendiği tarihte aynı kategoride bir finansal hedef var mı kontrol et
            val financialGoals = dbHelper.getAllFinancialGoalsByUserIdByCategoryId(userId ,  categoryId)
            financialGoals.forEach { goal ->
                if (goal.categoryId == categoryId) {
                    val updatedAmount = amount * (goal.percentage.toDouble() / 100)
                    goal.currentAmount += updatedAmount
                    dbHelper.updateFinancialGoal(goal)
                }
            }



            if (amount.equals(0.0)) {
                showSnackbar("Please enter an amount .")
                return@setOnClickListener
            }

            if (addRegularIncomeToDatabase(title, amount, currency, recurrence, date, categoryId,category,userCurrency)) {
                showSnackbar("Regular income added: $amount $currency")
                findNavController().navigate(R.id.action_addIncomeFragment_to_incomeFragment)
            } else {
                showSnackbar("Failed to add regular income.")
            }
        }
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

    private fun getSelectedCategory(): String {
        val selectedButtonId = toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun getSelectedCategoryId(): Int {
        val selectedCategoryName = getSelectedCategory()
        val dbHelper = DatabaseHelper(requireContext())
        val  selectedCategoryId =   dbHelper.getIncomeCategoryIdByCategoryName(selectedCategoryName)
        return selectedCategoryId
    }


    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
    fun createToggleButtonsForIncomeCategories() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val incomeCategories = dbHelper.getAllIncomeCategoriesByUserId(userId)

        for (category in incomeCategories) {
            val button = MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle)
            button.text = category
            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            button.setOnClickListener {

                showSnackbar("Clicked: $category")
            }


            binding.toggleButtonGroup.addView(button)
        }
    }




    fun addNewIncomeCategory() {
        val dbHelper = DatabaseHelper(requireContext())
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Add New Category")
        val input = EditText(requireContext())
        input.hint = "Category Name"
        alertDialog.setView(input)

        alertDialog.setPositiveButton("OK") { dialog, which ->
            val categoryName = input.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
                val userId = userData?.id
                val categoryId = userId?.let { dbHelper.addIncomeCategory(it, categoryName) }
                if (categoryId != -1L) {
                    // Kategori başarıyla eklendi
                    Toast.makeText(requireContext(), "Category added successfully", Toast.LENGTH_SHORT).show()
                    // ToggleButton grubunu temizle ve yeniden oluştur
                    clearToggleButtons()
                    createToggleButtonsForIncomeCategories()
                } else {
                    // Kategori eklenirken bir hata oluştu
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
    private fun clearToggleButtons() {
        binding.toggleButtonGroup.removeAllViews()
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

/*
    private fun scheduleAutomaticIncomes() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        // Kullanıcının regular gelirlerini al
        val regularIncomes = userData?.let { dbHelper.getAllRegularIncomesByUserId(it.id) }


        regularIncomes?.forEach { regularIncome ->

            when (regularIncome.recurrence) {
                "Once a week" -> {
                    // Haftalık gelir, her hafta ekle
                    scheduleWeeklyIncome(regularIncome)
                }
                "Once a month" -> {
                    // Aylık gelir, her ay ekle
                    scheduleMonthlyIncome(regularIncome)
                }

            }
        }
    }

    private fun scheduleWeeklyIncome(regularIncome: RegularIncome) {

        val startTime = System.currentTimeMillis()


        AlarmScheduler.scheduleRepeatingIncome(requireContext(), regularIncome.id, startTime, AlarmScheduler.ONCE_A_WEEK)
    }

    private fun scheduleMonthlyIncome(regularIncome: RegularIncome) {

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0) // Saat 00:00 olarak ayarla
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.DAY_OF_MONTH, 1) // Ayın ilk günü
        }.timeInMillis


        AlarmScheduler.scheduleRepeatingIncome(requireContext(), regularIncome.id, startTime, AlarmScheduler.ONCE_A_MONTH)
    }

 */



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}