package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentRegisterBinding
import com.mertyigit0.budgetmanager.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {


    private var _binding: FragmentSettingsBinding? = null;
    private val binding get() = _binding!!;

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding =   FragmentSettingsBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val curentUserEmail = currentUser?.email
        // SQLite veritabanından kullanıcı verilerini çekmek için uygun metodu çağırın
        val dbHelper = DatabaseHelper(requireContext())
        val userData = curentUserEmail?.let { dbHelper.getUserData(it) }

        if (userData != null) {
            binding.textView2.text = userData.email
        }
        }



    }


