package com.mertyigit0.budgetmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.ExpenseAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Expense
import com.mertyigit0.budgetmanager.databinding.FragmentExpenseListBinding

class ExpenseListFragment : Fragment() {
    private var _binding: FragmentExpenseListBinding? = null;
    private val binding get() = _binding!!;
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentExpenseListBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expenseAdapter = ExpenseAdapter(
            requireContext(),
            ArrayList()
        )


        val recyclerView = binding.expenseListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }
        // Gelir verilerini RecyclerView'a aktar
        expenses?.let { expenseAdapter.updateExpenseList(it) }





        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Kullanıcının seçtiği sıralama türü
                val selectedType = parent?.getItemAtPosition(position).toString()

                // Gelirleri seçilen tipe göre yeniden düzenle ve güncelle
                val sortedExpenses = sortExpensesByType(selectedType, expenses ?: listOf())
               expenseAdapter.updateExpenseList(sortedExpenses)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Bir şey seçilmediğinde yapılacaklar (opsiyonel)
            }
        }



    }

    private fun sortExpensesByType(type: String, expenses: List<Expense>): List<Expense> {
        return when (type) {
            "By Date" -> expenses.sortedBy { it.date }
            "By Amount" -> expenses.sortedByDescending { it.amount }
            "By Category" -> expenses.sortedBy { it.categoryName }
            // İsteğe bağlı olarak diğer sıralama türlerini buraya ekleyebilirsiniz
            else -> expenses // Herhangi bir geçerli sıralama türü yoksa, orijinal listeyi döndür
        }
    }
}