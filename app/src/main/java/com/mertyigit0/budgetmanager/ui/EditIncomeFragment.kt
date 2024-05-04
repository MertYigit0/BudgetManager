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
        val dbHelper = DatabaseHelper(requireContext())
        val navController = Navigation.findNavController(requireView())

        var income: Income? = null

        if (incomeId != null && incomeId != -1) {
            income = dbHelper.getIncomeById(incomeId)
        }

        if (income != null) {
            binding.amountEditText.setText(String.format("%.2f", income.amount))

            binding.editTextText.setText(income.note)
            binding.dateTextView.text = income.date

            binding.addButton.setOnClickListener {
                val updatedAmount = binding.amountEditText.text.toString().toDouble()
                val updatedDescription = binding.editTextText.text.toString()
                val updatedDate = binding.dateTextView.text.toString()

                // Güncellenmiş geliri oluştur
                val updatedIncome = Income(
                    id = incomeId!!, // Güncellenen gelirin kimliği
                    amount = updatedAmount,
                    note = updatedDescription,
                    date = updatedDate,
                    categoryId = income.categoryId,
                    createdAt = income?.createdAt ?: "",
                    userId = income.userId,
                    currency = income?.currency ?: "USD",
                    categoryName = income?.categoryName ?: ""
                )

                // Veritabanında güncelleme işlemi yap
                dbHelper.updateIncome(updatedIncome)

                // Finansal hedefleri gösteren fragmenta geri dön
                navController.navigate(R.id.action_editIncomeFragment_to_incomeFragment)
            }

        }


    }
}