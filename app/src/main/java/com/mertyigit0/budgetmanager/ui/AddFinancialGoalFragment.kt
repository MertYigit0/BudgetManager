package com.mertyigit0.budgetmanager.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentAddFinancialGoalBinding
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseBinding
import java.util.Calendar


class AddFinancialGoalFragment : Fragment() {
    private var _binding: FragmentAddFinancialGoalBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var dbHelper: DatabaseHelper
    private var selectedDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        dbHelper = DatabaseHelper(requireContext()) // Başlat

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFinancialGoalBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createToggleButtonsForIncomeCategories()

        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.addButton.setOnClickListener{
            addFinancialGoal()
        }


    }


    // Örnek olarak bir Fragment sınıfı içinde

    // Eklemek için tıklandığında çağrılan bir fonksiyon
    fun addFinancialGoal() {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val title = binding.titleEditText.text.toString() // Düzeltme: text özelliğini al
        val description = binding.editTextText.text.toString()
        val targetAmount =  binding.amountEditText.text.toString().toDouble()
        val currentAmount = 0.0
        val deadline = binding.dateTextView.text.toString()
        val categoryId = getSelectedCategoryId() // Kategori kimliğini almak için bir metot

        // Yeni bir FinancialGoal nesnesi oluştur
        val newGoal = FinancialGoal(
            id = 0, // ID veritabanında otomatik artan olduğu için sıfır geçiyoruz
            userId = userId,
            title = title,
            description = description,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            deadline = deadline,
            createdAt = "", // Bu alan veritabanında otomatik ayarlanacağı için boş geçilebilir
            categoryId = categoryId
        )

        // Yeni finansal hedefi veritabanına ekleyin
        val dbHelper = DatabaseHelper(requireContext()) // veya this.activity ifadesi kullanılabilir
        val success = dbHelper.addFinancialGoal(newGoal)

        if (success) {
            // Başarılı bir şekilde eklendiğine dair kullanıcıya geri bildirim ver
            Toast.makeText(requireContext(), "Financial goal successfully added!", Toast.LENGTH_SHORT).show()

            // Adapter oluşturulduktan sonra kullanılabilir
            val navController = Navigation.findNavController(requireView())
                navController.navigate(R.id.action_addFinancialGoalFragment_to_financialGoalFragment)



        } else {
            // Ekleme başarısız olduysa kullanıcıya geri bildirim ver
            Toast.makeText(requireContext(), "Failed to add financial goal", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getSelectedCategory(): String {
        val selectedButtonId = binding.toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }
    @SuppressLint("SuspiciousIndentation")
    private fun getSelectedCategoryId(): Int {
        val selectedCategoryName = getSelectedCategory()
        val dbHelper = DatabaseHelper(requireContext())
        val  selectedCategoryId =   dbHelper.getIncomeCategoryIdByCategoryName(selectedCategoryName)
        return selectedCategoryId
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

            }

            // ToggleGroup'a butonları ekleme işlemi
            binding.toggleButtonGroup.addView(button)
        }
    }


}