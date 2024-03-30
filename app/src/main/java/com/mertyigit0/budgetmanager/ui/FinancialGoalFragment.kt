package com.mertyigit0.budgetmanager.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.adapters.FinancialGoalAdapter
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.data.FinancialGoal
import com.mertyigit0.budgetmanager.databinding.FragmentAddExpenseBinding
import com.mertyigit0.budgetmanager.databinding.FragmentFinancialGoalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FinancialGoalFragment : Fragment() {

    private var _binding:FragmentFinancialGoalBinding? = null;
    private val binding get() = _binding!!;

    private val financialGoals = mutableListOf<FinancialGoal>()
    private lateinit var adapter: FinancialGoalAdapter
    private lateinit var dbHelper: DatabaseHelper
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFinancialGoalBinding.inflate(inflater,container,false)
        val view = binding.root;
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        // RecyclerView'i bul
        val recyclerView: RecyclerView = binding.financialGoalRecyclerView

        // Layout yöneticisini ayarla (Dikey olarak sıralama)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter oluşturulmadan önce öncelikle başlatılmalı
        adapter = FinancialGoalAdapter(financialGoals)

        // RecyclerView'e adapter'ı bağla
        recyclerView.adapter = adapter

        // Kullanıcının finansal hedeflerini al
        currentUserEmail?.let { email ->
            val userData = dbHelper.getUserData(email)
            userData?.let { user ->
                val goals = dbHelper.getAllFinancialGoalsByUserId(user.id)
                financialGoals.addAll(goals)
                adapter.notifyDataSetChanged()
            }
        }

        // Adapter oluşturulduktan sonra kullanılabilir
        binding.addFinancialGoalButton.setOnClickListener{
            showAddFinancialGoalDialog()
        }
    }



    // Yeni FinancialGoal eklemek için AlertDialog gösteren fonksiyon
    private fun showAddFinancialGoalDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.financial_goal_alert_dialog, null)
        builder.setView(dialogView)
            .setTitle("Add Financial Goal")
            .setPositiveButton("Add") { dialog, which ->
                val titleEditText: EditText = dialogView.findViewById(R.id.titleEditText)
                val descriptionEditText: EditText = dialogView.findViewById(R.id.descriptionEditText)
                val targetAmountEditText: EditText = dialogView.findViewById(R.id.targetAmountEditText)
                val deadlineEditText: EditText = dialogView.findViewById(R.id.deadlineEditText)

                // Kullanıcının girdiği bilgileri al
                val title = titleEditText.text.toString()
                val description = descriptionEditText.text.toString()
                val targetAmount = targetAmountEditText.text.toString().toDouble()
                val deadline = deadlineEditText.text.toString()
                val userId = currentUserEmail?.let { dbHelper.getUserData(it) }?.id ?: -1
                // Yeni bir FinancialGoal nesnesi oluştur
                val newFinancialGoal = FinancialGoal(
                    id = financialGoals.size + 1,
                    userId = userId, // Kullanıcı kimliğini buraya ekleyin
                    title = title,
                    description = description,
                    targetAmount = targetAmount,
                    currentAmount = 0.0, // Başlangıçta 0 olarak ayarlanabilir
                    deadline = deadline,
                    createdAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )

                // Oluşturulan hedefi listeye ekle
                financialGoals.add(newFinancialGoal)
                adapter.notifyDataSetChanged()


                addFinancialGoalToDatabase(newFinancialGoal)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }


    private fun addFinancialGoalToDatabase(financialGoal: FinancialGoal) {

        val success = dbHelper.addFinancialGoal(financialGoal)
        if (success) {


        } else {

        }
    }



}