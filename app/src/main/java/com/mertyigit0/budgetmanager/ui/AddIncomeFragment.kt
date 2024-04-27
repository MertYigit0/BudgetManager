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


    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


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
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)
        setupToggleButtonGroup()



        binding.regularIncomeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                addRegularIncome()
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



    private fun addIncomeToDatabase(amount: Double, category: String,categoryId : Int, date: String, description: String?, currency: String): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1

        // Döviz kuru veritabanından al
        val exchangeRate = dbHelper.getExchangeRate(currency)

        // Dönüştürülmüş miktarı hesapla ve en fazla 2 basamakla sınırla
        val convertedAmount = (amount / exchangeRate).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        // Dönüştürülmüş miktarı, para birimiyle birlikte ekranda göstermek için string oluştur
        val equivalentAmountText = "%.2f".format(convertedAmount) + " USD"






        val income = Income(id = 0, userId = userId, amount = convertedAmount, currency ="USD", categoryId = categoryId, categoryName = category, date = date, note = description ?: "", createdAt = "")

        val databaseHelper = DatabaseHelper(requireContext())


        val isSuccess = databaseHelper.addIncome(income)

        // Eğer eklenme başarılı ise
        if (isSuccess) {
            // Snackbar'da dönüştürülmüş miktarı ve para birimini göster
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
                    goal.currentAmount += updatedAmount // Finansal hedefin mevcut tutarına ekleyin
                    dbHelper.updateFinancialGoal(goal) // Güncellenmiş finansal hedefi veritabanına kaydet
                }
            }

            if (addIncomeToDatabase(amount, category,categoryId, date, description,currency)) {
                showSnackbar("Income added: $amount $currency")
                findNavController().navigate(R.id.action_addIncomeFragment_to_incomeFragment)
            } else {
                showSnackbar("Failed to add income.")
            }
        }
    }


    private fun addRegularIncomeToDatabase(title: String, amount: Double, currency: String, recurrence: String, date: String, categoryId: Int): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val regularIncome = RegularIncome(id = 0, userId = userId, title = title, amount = amount, currency = currency, recurrence = recurrence, date = date, categoryId = categoryId)

        return dbHelper.addRegularIncome(regularIncome)
    }

    private fun addRegularIncome() {
        binding.addButton.setOnClickListener {
            val title = getSelectedCategory()
            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val currency = binding.currencySpinner.selectedItem.toString()
            val recurrence = binding.regularIncomeSpinner.selectedItem.toString()
            val date = getCurrentDate()
            val categoryId = getSelectedCategoryId()

            if (categoryId.equals(-1)) {
                showSnackbar("Please select a category.")
                return@setOnClickListener
            }
            if (amount.equals(0.0)) {
                showSnackbar("Please enter an amount .")
                return@setOnClickListener
            }

            if (addRegularIncomeToDatabase(title, amount, currency, recurrence, date, categoryId)) {
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
                // Buttona tıklandığında yapılacak işlemler
                showSnackbar("Clicked: $category")
            }

            // ToggleGroup'a butonları ekleme işlemi
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}