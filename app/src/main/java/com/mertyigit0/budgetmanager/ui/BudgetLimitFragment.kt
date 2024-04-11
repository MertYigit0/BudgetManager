package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.databinding.FragmentBudgetLimitBinding
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseBinding


class BudgetLimitFragment : Fragment() {
    private var _binding: FragmentBudgetLimitBinding? = null;
    private val binding get() = _binding!!;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetLimitBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}