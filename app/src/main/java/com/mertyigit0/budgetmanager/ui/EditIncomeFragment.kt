package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income
import com.mertyigit0.budgetmanager.data.RegularIncome
import com.mertyigit0.budgetmanager.databinding.FragmentEditIncomeBinding
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeBinding


class EditIncomeFragment : Fragment() {

    private var _binding: FragmentEditIncomeBinding? = null;
    private val binding get() = _binding!!;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditIncomeBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val incomeId = arguments?.getInt("incomeId", -1)
        val regularIncomeId = arguments?.getInt("regularIncomeId", -1)
        val dbHelper = DatabaseHelper(requireContext())
        val navController = Navigation.findNavController(requireView())

        if (incomeId != null && incomeId != -1) {
            // Eğer incomeId değeri mevcut ve -1 değilse, bir gelir düzenleme ekranı açılıyor
            val income = dbHelper.getIncomeById(incomeId)

            // Gelir verilerini görsel öğelerle doldur
            if (income != null) {
                binding.amountEditText.setText(String.format("%.2f", income.amount))
                binding.editTextText.setText(income.note)
                binding.dateTextView.text = income.date

                // Düzenleme butonuna tıklandığında yapılacak işlemler
                binding.addButton.setOnClickListener {
                    val updatedAmount = binding.amountEditText.text.toString().toDouble()
                    val updatedDescription = binding.editTextText.text.toString()
                    val updatedDate = binding.dateTextView.text.toString()

                    // Güncellenmiş geliri oluştur
                    val updatedIncome = Income(
                        id = incomeId, // Güncellenen gelirin kimliği
                        amount = updatedAmount,
                        note = updatedDescription,
                        date = updatedDate,
                        categoryId = income.categoryId,
                        createdAt = income.createdAt ?: "",
                        userId = income.userId,
                        currency = income.currency ?: "USD",
                        categoryName = income.categoryName ?: ""
                    )

                    // Veritabanında güncelleme işlemi yap
                    dbHelper.updateIncome(updatedIncome)

                    // Finansal hedefleri gösteren fragmenta geri dön
                    navController.navigate(R.id.action_editIncomeFragment_to_incomeFragment)
                }
            }
        } else if (regularIncomeId != null && regularIncomeId != -1) {
            // Eğer regularIncomeId değeri mevcut ve -1 değilse, bir düzenli gelir düzenleme ekranı açılıyor
            val regularIncome = dbHelper.getRegularIncomeById(regularIncomeId)

            // Düzenli gelir verilerini görsel öğelerle doldur
            if (regularIncome != null) {
                binding.amountEditText.setText(String.format("%.2f", regularIncome.amount))
                binding.editTextTitle.setText(regularIncome.title)
                binding.dateTextView.text = regularIncome.date

                // Düzenleme butonuna tıklandığında yapılacak işlemler
                binding.addButton.setOnClickListener {
                    val updatedAmount = binding.amountEditText.text.toString().toDouble()
                    val updatedTitle = binding.editTextTitle.text.toString()
                    val updatedDate = binding.dateTextView.text.toString()

                    // Güncellenmiş düzenli geliri oluştur
                    val updatedRegularIncome = RegularIncome(
                        id = regularIncomeId, // Güncellenen düzenli gelirin kimliği
                        amount = updatedAmount,
                        date = updatedDate,
                        categoryId = regularIncome.categoryId,
                        userId = regularIncome.userId,
                        currency = regularIncome.currency ?: "USD",
                        categoryName = regularIncome.categoryName ?: "",
                        recurrence = regularIncome.recurrence ?: "",
                        title = updatedTitle
                    )

                    // Veritabanında güncelleme işlemi yap
                    dbHelper.updateRegularIncome(updatedRegularIncome)

                    // Finansal hedefleri gösteren fragmenta geri dön
                    navController.navigate(R.id.action_editIncomeFragment_to_incomeFragment)
                }
            }
        }

    }




}