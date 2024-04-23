package com.mertyigit0.budgetmanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mertyigit0.budgetmanager.MainActivity
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.databinding.FragmentInfo2Binding
import com.mertyigit0.budgetmanager.databinding.FragmentLoginBinding

class Info2Fragment : Fragment() {
    private var _binding: FragmentInfo2Binding? = null;
    private val binding get() = _binding!!;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentInfo2Binding.inflate(inflater,container,false)
        val view = binding.root;
        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }



}