package com.mertyigit0.budgetmanager.currency

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.data.DatabaseHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CurrencyListAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currency, container, false)
        recyclerView = view.findViewById(R.id.currencyList_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = CurrencyListAdapter(emptyMap())
        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchData()
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
            showSnackbar("Veriler SharedPreferences'tan alındı.")
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

    // API'den alınan verileri SQLite veritabanına kaydeden kod parçası
    private fun saveExchangeRatesToDatabase(exchangeRates: Map<String, Double>) {
        val dbHelper = DatabaseHelper(requireContext())
        for ((currencyCode, rate) in exchangeRates) {
            dbHelper.addExchangeRate(currencyCode, rate)
        }
    }

    private fun updateUI(currencyRates: Map<String, Double>) {
        adapter = CurrencyListAdapter(currencyRates)
        recyclerView.adapter = adapter
    }

    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}
