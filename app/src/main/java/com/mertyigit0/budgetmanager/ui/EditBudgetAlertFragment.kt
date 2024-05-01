package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentEditBudgetAlertBinding
import com.mertyigit0.budgetmanager.databinding.FragmentLoginBinding


class EditBudgetAlertFragment : Fragment() {
    private var _binding: FragmentEditBudgetAlertBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentEditBudgetAlertBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val budgetAlertId = arguments?.getInt("budgetAlertId", -1)
        dbHelper = DatabaseHelper(requireContext())
        val navController = Navigation.findNavController(requireView())

        var budgetAlert: BudgetAlert? = null

        if (budgetAlertId != null) {
            budgetAlert = dbHelper.getBudgetAlertById(budgetAlertId)


        }
        if (budgetAlert != null) {
            // BudgetAlert verilerini görüntüleme
            binding.editTextText.setText(budgetAlert.message.toString())
            binding.targetAmountEditText.setText(budgetAlert.targetAmount.toString())


            binding.addButton.setOnClickListener {
                val updatedMessage = binding.editTextText.text.toString()
                val updatedTargetAmount = binding.targetAmountEditText.text.toString().toDouble()


                val updatedBudgetAlert = BudgetAlert(
                    id = budgetAlertId!!,
                    userId = budgetAlert.userId,
                    categoryId = budgetAlert.categoryId,
                    alertType = budgetAlert.alertType,
                    message = updatedMessage,
                    targetAmount = updatedTargetAmount,
                    currentAmount = budgetAlert.currentAmount,
                    createdAt = budgetAlert.createdAt
                )


                dbHelper.updateBudgetAlert(updatedBudgetAlert)


                navController.navigate(R.id.action_editBudgetAlertFragment_to_budgetAlertFragment)
            }
        }


    }


}