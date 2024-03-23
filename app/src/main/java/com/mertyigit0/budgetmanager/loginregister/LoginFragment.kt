package com.mertyigit0.budgetmanager.loginregister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mertyigit0.budgetmanager.R
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mertyigit0.budgetmanager.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null;
    private val binding get() = _binding!!;

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())

        binding.loginButton.setOnClickListener{
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                // E-posta veya şifre boşsa kullanıcıya uyarı ver
                Toast.makeText(requireContext(), "Lütfen e-posta ve şifreyi doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                // Şifre en az 6 karakter olmalı
                Toast.makeText(requireContext(), "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    // Giriş başarılı ise yapılacak işlemler

                  // navController.navigate(R.id.action_loginFragment_to_incomeFragment)
                    navController.navigate(R.id.action_loginFragment_to_addIncomeFragment)
                } else {
                    // Giriş başarısız ise kullanıcıya hata mesajı gösterebilirsiniz
                    Toast.makeText(requireContext(), "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }




            }





        }

        binding.registerNowText.setOnClickListener{
            navController.navigate(R.id.action_loginFragment_to_registerFragment)

        }
        binding.registerText.setOnClickListener{
            navController.navigate(R.id.action_loginFragment_to_registerFragment)

        }


    }





}