package com.mertyigit0.budgetmanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mertyigit0.budgetmanager.MainActivity
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.databinding.FragmentInfo1Binding
import com.mertyigit0.budgetmanager.databinding.FragmentLoginBinding


class Info1Fragment : Fragment() {

    private var _binding: FragmentInfo1Binding? = null;
    private val binding get() = _binding!!;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentInfo1Binding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        binding.nextButton.setOnClickListener{
            navController.navigate(R.id.action_info1Fragment_to_info2Fragment)




        }

    }

}