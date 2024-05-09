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
import com.mertyigit0.budgetmanager.adapters.ExpenseAdapter
import com.mertyigit0.budgetmanager.data.CombinedExpense
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.Expense
import com.mertyigit0.budgetmanager.data.RecurringPayment
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
        val navController = Navigation.findNavController(requireView())
        expenseAdapter = ExpenseAdapter(
            requireContext(),
            ArrayList(),
            navController
        )


        val recyclerView = binding.expenseListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = expenseAdapter

        val dbHelper = DatabaseHelper(requireContext())

        val currentUserEmail = auth.currentUser?.email
        val userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Veritabanından tüm gelirleri al
        val expenses = userData?.let { dbHelper.getAllExpensesByUserId(it.id) }
        val regularExpenses = userData?.let { dbHelper.getAllRecurringPaymentsByUserId(it.id) }
        val combinedExpenses =  regularExpenses?.let { combineExpensesAndRecurringPayments(expenses ?: listOf(), it) }
        // Gelir verilerini RecyclerView'a aktar
        combinedExpenses?.let { expenseAdapter.updateExpenseList(it) }





        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Kullanıcının seçtiği sıralama türü
                val selectedType = parent?.getItemAtPosition(position).toString()

                // Gelirleri seçilen tipe göre yeniden düzenle ve güncelle
                val sortedExpenses = sortExpensesByType(selectedType, combinedExpenses ?: listOf())
               expenseAdapter.updateExpenseList(sortedExpenses)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Bir şey seçilmediğinde yapılacaklar (opsiyonel)
            }
        }



    }

    private fun sortExpensesByType(type: String, expenses: List<CombinedExpense>): List<CombinedExpense> {
        return when (type) {
            "By Date" -> expenses.sortedBy { it.date }
            "By Amount" -> expenses.sortedByDescending { it.amount }
            "By Category" -> expenses.sortedBy { it.categoryName }
            // İsteğe bağlı olarak diğer sıralama türlerini buraya ekleyebilirsiniz
            else -> expenses // Herhangi bir geçerli sıralama türü yoksa, orijinal listeyi döndür
        }
    }



    fun combineExpensesAndRecurringPayments(expenses: List<Expense>, recurringPayments: List<RecurringPayment>): List<CombinedExpense> {
        val combinedList = mutableListOf<CombinedExpense>()

        // Giderleri CombinedExpense türüne dönüştür ve birleştir
        expenses.forEach { expense ->
            combinedList.add(
                CombinedExpense(
                    id = expense.id,
                    userId = expense.userId,
                    title = null,  // RecurringPayment alanları null olacak
                    amount = expense.amount,
                    currency = expense.currency,
                    recurrence = null,  // RecurringPayment alanları null olacak
                    date = expense.date,
                    categoryId = expense.categoryId,
                    categoryName = expense.categoryName,
                    note = expense.note,
                    createdAt = expense.createdAt
                )
            )
        }

        // Tekrarlayan ödemeleri CombinedExpense türüne dönüştür ve birleştir
        recurringPayments.forEach { recurringPayment ->
            combinedList.add(
                CombinedExpense(
                    id = recurringPayment.id,
                    userId = recurringPayment.userId,
                    title = recurringPayment.title,
                    amount = recurringPayment.amount,
                    currency = recurringPayment.currency,
                    recurrence = recurringPayment.recurrence,
                    date = recurringPayment.nextPaymentDate,  // Tekrarlayan ödemenin sonraki ödeme tarihini alın
                    categoryId = recurringPayment.categoryId,
                    categoryName = recurringPayment.categoryName,  // Tekrarlayan ödemeleri diğerlerinden ayırmak için boş bir kategori adı belirleyin
                    note = null,  // Tekrarlayan ödeme alanları null olacak
                    createdAt = ""
                )
            )
        }

        return combinedList
    }

}