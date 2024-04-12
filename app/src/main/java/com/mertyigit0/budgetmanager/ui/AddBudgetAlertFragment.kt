package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.BudgetAlert
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentAddBudgetAlertBinding
import com.mertyigit0.budgetmanager.databinding.FragmentBudgetAlertBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddBudgetAlertFragment : Fragment() {

    private var _binding: FragmentAddBudgetAlertBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddBudgetAlertBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)

        setupToggleButtonGroup()
        addBudgetAlert()

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


    private fun addBudgetAlertToDatabase(alertType: String, message: String, targetAmount: Double, currentAmount: Double, ): Boolean {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        val budgetAlert = BudgetAlert(
            id = 0,
            userId = userId,
            alertType = alertType,
            message = message,
            targetAmount = targetAmount,
            currentAmount = currentAmount,
            createdAt = "", // Burası için geçici olarak boş bir değer atıyorum, bu değeri veritabanında otomatik olarak oluşturulabilirsiniz.
            categoryId = 0
        )

        val databaseHelper = DatabaseHelper(requireContext())
        return databaseHelper.addBudgetAlert(budgetAlert)
    }

    private fun addBudgetAlert() {
        binding.addButton.setOnClickListener {
            val alertType = "your_alert_type_here" // Alert tipini buraya ekleyin
            val message = "your_message_here" // Mesajı buraya ekleyin
            val targetAmount = binding.targetAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val currentAmount = 1.0

            if (addBudgetAlertToDatabase(alertType, message, targetAmount, currentAmount)) {
                showSnackbar("Budget alert added: $message")
                findNavController().navigate(R.id.action_addBudgetAlertFragment_to_budgetAlertFragment)
            } else {
                showSnackbar("Failed to add budget alert.")
            }
        }
    }





    private fun getSelectedCategory(): String {
        val selectedButtonId = toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}