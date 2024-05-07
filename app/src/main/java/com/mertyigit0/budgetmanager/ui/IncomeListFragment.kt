package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.IncomeAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Income
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeBinding
import com.mertyigit0.budgetmanager.databinding.FragmentIncomeListBinding


class IncomeListFragment : Fragment() {

    private var _binding: FragmentIncomeListBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var auth: FirebaseAuth
    private lateinit var incomeAdapter: IncomeAdapter


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
        _binding = FragmentIncomeListBinding.inflate(inflater, container, false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(requireView())
        incomeAdapter = IncomeAdapter(
            requireContext(),
            ArrayList(),
            navController

        ) // Boş bir ArrayList ile IncomeAdapter oluştur

        val recyclerView = binding.incomeListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = incomeAdapter

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val incomes = userData?.let { dbHelper.getAllIncomesByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
      //  incomes?.let { incomeAdapter.updateIncomeList(it) }





        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Kullanıcının seçtiği sıralama türü
                val selectedType = parent?.getItemAtPosition(position).toString()

                // Gelirleri seçilen tipe göre yeniden düzenle ve güncelle
                val sortedIncomes = sortIncomesByType(selectedType, incomes ?: listOf())
              //  incomeAdapter.updateIncomeList(sortedIncomes)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Bir şey seçilmediğinde yapılacaklar (opsiyonel)
            }
        }



    }

    private fun sortIncomesByType(type: String, incomes: List<Income>): List<Income> {
        return when (type) {
            "By Date" -> incomes.sortedBy { it.date }
            "By Amount" -> incomes.sortedByDescending { it.amount }
            "By Category" -> incomes.sortedBy { it.categoryName }
            // İsteğe bağlı olarak diğer sıralama türlerini buraya ekleyebilirsiniz
            else -> incomes // Herhangi bir geçerli sıralama türü yoksa, orijinal listeyi döndür
        }
    }
}


