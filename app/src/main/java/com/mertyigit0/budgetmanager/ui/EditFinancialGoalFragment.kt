package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentEditFinancialGoalBinding
import com.mertyigit0.budgetmanager.databinding.FragmentLoginBinding


class EditFinancialGoalFragment : Fragment() {
    private var _binding: FragmentEditFinancialGoalBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditFinancialGoalBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val financialGoalId = arguments?.getInt("financialGoalId", -1)
        dbHelper = DatabaseHelper(requireContext())
        val navController = Navigation.findNavController(requireView())

        var financialGoal: FinancialGoal? = null

        if (financialGoalId != null) {
            financialGoal = dbHelper.getFinancialGoalById(financialGoalId)
        }


        if (financialGoal != null) {
            binding.amountEditText.setText(financialGoal.targetAmount.toString())
            binding.dateTextView.setText(financialGoal.deadline.toString())
            binding.editTextText.setText(financialGoal.description)
            binding.titleEditText.setText(financialGoal.title)
            binding.percentageEditTextNumber.setText(financialGoal.percentage.toString())

        }


        binding.addButton.setOnClickListener{
            val updatedTitle = binding.titleEditText.text.toString()
            val updatedDescription = binding.editTextText.text.toString()
            val updatedTargetAmount = binding.amountEditText.text.toString().toDouble()
            val updatedDeadline = binding.dateTextView.text.toString()
            val updatedPercentage = binding.percentageEditTextNumber.text.toString().toInt()

            // Mevcut finansal hedefin değerlerini korumak için
            val updatedFinancialGoal = FinancialGoal(
                id = financialGoalId!!, // Güncellenen finansal hedefin kimliği
                title = updatedTitle,
                description = updatedDescription,
                targetAmount = updatedTargetAmount,
                deadline = updatedDeadline,
                percentage = updatedPercentage,
                // Diğer alanlar için mevcut değerleri atayabilirsiniz veya boş bırakabilirsiniz
                categoryId = financialGoal?.categoryId ?: 0,
                createdAt = financialGoal?.createdAt ?: "",
                currentAmount = financialGoal?.currentAmount ?: 0.0,
                photo = financialGoal?.photo ?: byteArrayOf(),
                userId = financialGoal?.userId ?: 0,
                currency = financialGoal?.currency ?:"USD"
            )

            // Veritabanında güncelleme işlemi yap
            dbHelper.updateFinancialGoal(updatedFinancialGoal)

            navController.navigate(R.id.action_editFinancialGoalFragment_to_financialGoalFragment)

        }






    }


}