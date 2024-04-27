package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentProfileBinding
import com.mertyigit0.budgetmanager.databinding.FragmentSettingsBinding


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val navController by lazy { Navigation.findNavController(requireView()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        // FirebaseAuth örneğini al
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.itemSettings.setOnClickListener{
            navController.navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        binding.itemLogout.setOnClickListener{
            auth.signOut()
            redirectToLoginScreen()
        }
        displayUserData()

    }

    private fun redirectToLoginScreen() {
        navController.navigate(R.id.action_profileFragment_to_loginFragment)
    }

    fun displayUserData() {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }

        if (userData != null) {
            binding.textView12.text = "Email: ${userData.email}"+"User ID: ${userData.id}"

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}