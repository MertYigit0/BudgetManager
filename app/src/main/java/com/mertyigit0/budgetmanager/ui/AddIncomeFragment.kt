package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mertyigit0.budgetmanager.databinding.FragmentAddIncomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random


class AddIncomeFragment : Fragment() {

    private var _binding: FragmentAddIncomeBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var toggleButtonGroup: MaterialButtonToggleGroup

    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


            val dbHelper = DatabaseHelper(requireContext())
            val userId = currentUserEmail?.let { it1 -> dbHelper.getUserData(it1) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAddIncomeBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)

        setupToggleButtonGroup()
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



    private fun addIncome() {
        binding.addButton.setOnClickListener {


            val amount = binding.amountEditText.text.toString().toDoubleOrNull() ?: 0.0
            val category = getSelectedCategory()
            val date = getCurrentDate()
            val description = binding.editTextText.text.toString()

            val income = createIncome(amount, category, date, description)
            if (addIncomeToDatabase(income)) {
                showSnackbar("Income added: $income")
                navigateToIncomeFragment(amount.toFloat(), category)
            } else {
                showSnackbar("Failed to add income.")
            }
        }
    }

    private fun addIncomeToDatabase(income: Income): Boolean {
        val databaseHelper = DatabaseHelper(requireContext())
        return databaseHelper.addIncome(income)
    }

    fun createIncome(amount: Double, category: String, date: String, description: String?): Income {
        val dbHelper = DatabaseHelper(requireContext())
        val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
        return Income(id = 0, userId = userId, amount = amount, currency = "", categoryId = 0, date = date, note = description ?: "", createdAt = "")
    }

    private fun getSelectedCategory(): String {
        val selectedButtonId = toggleButtonGroup.checkedButtonId
        return view?.findViewById<MaterialButton>(selectedButtonId)?.text.toString()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun navigateToIncomeFragment(amount: Float, category: String) {
        val bundle = Bundle().apply {
            putFloat("amount", amount)
            putString("category", category)
        }

        findNavController().navigate(R.id.action_addIncomeFragment_to_incomeFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}