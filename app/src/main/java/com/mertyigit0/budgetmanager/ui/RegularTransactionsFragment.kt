package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.ExpenseAdapter
import com.mertyigit0.budgetmanager.adapters.RegularExpenseAdapter
import com.mertyigit0.budgetmanager.adapters.RegularIncomeAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeListBinding
import com.mertyigit0.budgetmanager.databinding.FragmentRegularTransactionsBinding


class RegularTransactionsFragment : Fragment() {

    private var _binding: FragmentRegularTransactionsBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var regularIncomeAdapter: RegularIncomeAdapter
    private lateinit var regularExpenseAdapter: RegularExpenseAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegularTransactionsBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        regularIncomeAdapter = RegularIncomeAdapter(
            requireContext(),
            ArrayList(),
            navController
        )
        regularExpenseAdapter = RegularExpenseAdapter(
            requireContext(),
            ArrayList(),
            navController
        )


        val recyclerView = binding.regularIncomeRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = regularIncomeAdapter

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val regularIncomes = userData?.let { dbHelper.getAllRegularIncomesByUserId(it.id) }
        regularIncomes?.let { regularIncomeAdapter.updateIncomeList(it) }

        if (userData != null) {
            val totalIncome = dbHelper.getTotalRegularIncomeByUserId(userData.id)
            val formattedTotalIncome = String.format("%.2f", totalIncome) // İki ondalık haneyle biçimlendirilmiş bir string oluşturur
            val totalIncomeText = "Total Regular Income: $formattedTotalIncome"
            binding.totalRegularIncomes.text = totalIncomeText
        }





        val recyclerView2 = binding.regularExpenseRecyclerView
        recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        recyclerView2.adapter = regularExpenseAdapter

        val regularExpenses = userData?.let { dbHelper.getAllRecurringPaymentsByUserId(it.id) }
        regularExpenses?.let { regularExpenseAdapter.updateExpenseList(it) }

        if (userData != null) {
            val totalRecurringPayment = dbHelper.getTotalRecurringPaymentByUserId(userData.id)
            val formattedTotalRecurringPayment = String.format("%.2f", totalRecurringPayment)
            val totalRecurringPaymentText = "Total Recurring Payments: $formattedTotalRecurringPayment"
            binding.totalRegularExpenses.text = totalRecurringPaymentText
        }


    }


}