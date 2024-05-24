package com.mertyigit0.budgetmanager.ui

import BudgetAlertAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.IncomeAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentAddIncomeBinding
import com.mertyigit0.budgetmanager.databinding.FragmentBudgetAlertBinding


class BudgetAlertFragment : Fragment() {
    private var _binding: FragmentBudgetAlertBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var auth: FirebaseAuth
    private lateinit var budgetAdapter: BudgetAlertAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentBudgetAlertBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())


       budgetAdapter = BudgetAlertAdapter(requireContext(),ArrayList(),navController)

        val recyclerView = binding.budgetAlertRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = budgetAdapter

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val incomes = userData?.let { dbHelper.getAllBudgetAlertsByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
        incomes?.let { budgetAdapter.updateBudgetAlertList(it) }


        binding.addBudgerAlertButton.setOnClickListener{
            navController.navigate(R.id.action_budgetAlertFragment_to_addBudgetAlertFragment)
        }


    }


}