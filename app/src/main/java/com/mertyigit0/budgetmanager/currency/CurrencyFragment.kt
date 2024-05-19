package com.mertyigit0.budgetmanager.currency

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import com.mertyigit0.budgetmanager.databinding.FragmentCurrencyBinding
import com.mertyigit0.budgetmanager.databinding.FragmentProfileBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CurrencyListAdapter
    private var _binding: FragmentCurrencyBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        val view = binding.root

        // Spinner'ı ayarla
        val spinner = binding.spinner2
        spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.currency_list, android.R.layout.simple_spinner_dropdown_item)

        // Varsayılan seçimi kaldır
        spinner.setSelection(0, false) // 0: Varsayılan olarak seçilecek öğenin pozisyonu

        // RecyclerView'ı bağlayın ve ayarlayın
        binding.currencyListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.currencyListRecyclerView.adapter = CurrencyListAdapter(emptyMap())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()


        var dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        var userCurrency = userData?.currency
        userCurrency?.let { currency ->
            val currencyIndex = getIndexOfCurrencyInSpinner(currency)
            binding.spinner2.setSelection(currencyIndex)
        }



        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @SuppressLint("SuspiciousIndentation")
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCurrency = parent?.getItemAtPosition(position).toString()

                 userCurrency = userData?.currency
                    if (selectedCurrency !=userCurrency) {
                        updateCurrency(selectedCurrency)
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Bir şey seçilmediğinde yapılacak işlemi tanımlayabilirsiniz (opsiyonel)
            }


        }

    }

    // Kullanıcı para birimini güncellemek için bu fonksiyonu kullanabilirsiniz
    fun updateCurrency(selectedCurrency: String) {
        // Mevcut kullanıcının e-posta adresini alın
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val dbHelper = DatabaseHelper(requireContext())
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        // Eğer mevcut kullanıcı e-posta adresi yoksa veya seçilen para birimi boşsa işlemi yapmayın
        if (currentUserEmail.isNullOrEmpty() || selectedCurrency.isNullOrEmpty()) {
            return
        }

        // Veritabanında kullanıcının para birimini güncellemek için updateCurrencyUser fonksiyonunu çağırın

        val isSuccess = dbHelper.updateCurrencyUser(currentUserEmail, selectedCurrency)

        // Başarılı bir şekilde güncellendiğini kontrol edin ve kullanıcıya bir geri bildirim gösterin
        if (isSuccess) {
            Toast.makeText(requireContext(), "Currency updated successfully", Toast.LENGTH_SHORT).show()
            // Tüm işlemleri yeni para birimine dönüştür
            val oldCurrency = userData?.currency
            if (oldCurrency != null) {
                updateCurrencyForAllTransactions(oldCurrency, selectedCurrency)
                updateCurrencyForAllFinancialGoals(oldCurrency, selectedCurrency)
                updateCurrencyForAllBudgetAlerts(oldCurrency,selectedCurrency)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to update currency", Toast.LENGTH_SHORT).show()
        }
    }


    private fun fetchData() {
        val sharedPreferences = requireContext().getSharedPreferences("CurrencyPrefs", Context.MODE_PRIVATE)

        // Önceki veri çekme tarihini al
        val lastFetchTime = sharedPreferences.getLong("lastFetchTime", 0)

        // Şu anki zamanı al
        val currentTime = System.currentTimeMillis()

        // Bir günün milisaniye cinsinden karşılığı
        val oneDayInMillis = 24 * 60 * 60 * 1000

        // Geçen zamanı hesapla (milisaniye cinsinden)
        val elapsedTime = currentTime - lastFetchTime

        // Eğer geçen zaman bir günden fazlaysa, verileri SQLite'dan al
        if (elapsedTime > oneDayInMillis || lastFetchTime == 0L) {
            fetchCurrencyFromAPI()

            // Son veri çekme tarihini güncelle
            sharedPreferences.edit().putLong("lastFetchTime", currentTime).apply()
        } else {
            // Eğer bir gün geçmemişse ve önceki bir veri çekme tarihi varsa, verileri SQLite'dan al
            val dbHelper = DatabaseHelper(requireContext())
            val exchangeRates = dbHelper.getAllExchangeRates()
            updateUI(exchangeRates)
            //showSnackbar("Veriler SharedPreferences'tan alındı.")
        }
    }


    private fun fetchCurrencyFromAPI() {
        CurrencyApiClient.service.getCurrencyRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    val currencyResponse = response.body()
                    currencyResponse?.let {
                        updateUI(it.rates)
                        saveExchangeRatesToDatabase(it.rates)
                        showSnackbar("Veriler API'den geldi.")
                    }
                } else {
                    // İstek başarısız oldu
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                // Hata oluştu
            }
        })
    }

    // API'den alınan verileri SQLite veritabanına kaydet
    private fun saveExchangeRatesToDatabase(exchangeRates: Map<String, Double>) {
        val dbHelper = DatabaseHelper(requireContext())
        for ((currencyCode, rate) in exchangeRates) {
            dbHelper.addExchangeRate(currencyCode, rate)
        }
    }

    private fun updateUI(currencyRates: Map<String, Double>) {
        adapter = CurrencyListAdapter(currencyRates)
        binding.currencyListRecyclerView.adapter = adapter
    }
    private fun getIndexOfCurrencyInSpinner(currency: String): Int {
        val spinnerAdapter = binding.spinner2.adapter
        val count = spinnerAdapter.count
        for (i in 0 until count) {
            if (spinnerAdapter.getItem(i).toString() == currency) {
                return i
            }
        }
        return 0 // Varsayılan olarak 0. indeksi döndür
    }


    private fun updateCurrencyForAllTransactions(oldCurrency: String, newCurrency: String) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val userId = userData?.id
        // Gelirleri güncelle
        val incomes = userId?.let { dbHelper.getAllIncomesByUserId(it) }
        if (incomes != null) {
            for (income in incomes) {
                if (income.currency == oldCurrency) {
                    val amountInUSD = income.amount / dbHelper.getExchangeRate(oldCurrency)
                    val convertedAmount = amountInUSD * dbHelper.getExchangeRate(newCurrency)
                    income.amount = convertedAmount
                    income.currency = newCurrency
                    dbHelper.updateIncome(income)
                }
            }
        }

        // Giderleri güncelle
        val expenses = userId?.let { dbHelper.getAllExpensesByUserId(it) }
        if (expenses != null) {
            for (expense in expenses) {
                if (expense.currency == oldCurrency) {
                    val amountInUSD = expense.amount / dbHelper.getExchangeRate(oldCurrency)
                    val convertedAmount = amountInUSD * dbHelper.getExchangeRate(newCurrency)
                    expense.amount = convertedAmount
                    expense.currency = newCurrency
                    dbHelper.updateExpense(expense)
                }
            }
        }
    }


    private fun updateCurrencyForAllFinancialGoals(oldCurrency: String, newCurrency: String) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val userId = userData?.id

        // Tüm finansal hedefleri güncelle
        val financialGoals = userId?.let { dbHelper.getAllFinancialGoalsByUserId(it) }
        financialGoals?.forEach { financialGoal ->
            if (financialGoal.currency == oldCurrency) {
                // Eski para biriminden USD'ye ve ardından USD'den yeni para birimine dönüştürme işlemi
                val amountInUSD = financialGoal.targetAmount / dbHelper.getExchangeRate(oldCurrency)
                val convertedAmount = amountInUSD * dbHelper.getExchangeRate(newCurrency)
                financialGoal.currentAmount = financialGoal.currentAmount / dbHelper.getExchangeRate(oldCurrency) * dbHelper.getExchangeRate(newCurrency)


                // Yeni dönüştürülmüş miktarı ata
                financialGoal.targetAmount = convertedAmount
                financialGoal.currency = newCurrency

                // FinancialGoal'u güncelle
                dbHelper.updateFinancialGoal(financialGoal)
            }
        }
    }

    private fun updateCurrencyForAllBudgetAlerts(oldCurrency: String, newCurrency: String) {
        val dbHelper = DatabaseHelper(requireContext())
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var userData = currentUserEmail?.let { dbHelper.getUserData(it) }
        val userId = userData?.id

        // Tüm bütçe uyarılarını güncelle
        val budgetAlerts = userId?.let { dbHelper.getAllBudgetAlertsByUserId(it) }
        budgetAlerts?.forEach { budgetAlert ->
            if (budgetAlert.currency == oldCurrency) {
                // Eski para biriminden USD'ye ve ardından USD'den yeni para birimine dönüştürme işlemi
                val amountInUSD = budgetAlert.targetAmount / dbHelper.getExchangeRate(oldCurrency)
                val convertedAmount = amountInUSD * dbHelper.getExchangeRate(newCurrency)

                // Yeni dönüştürülmüş miktarı ata
                budgetAlert.targetAmount = convertedAmount
                budgetAlert.currency = newCurrency

                // Bütçe uyarısını güncelle
                dbHelper.updateBudgetAlert(budgetAlert)
            }
        }
    }





    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}
