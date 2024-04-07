package com.mertyigit0.budgetmanager.currency

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
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CurrencyListAdapter
    private val cacheManager = CurrencyCacheManager()

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
        val cachedData = cacheManager.getCachedData()
        if (cachedData != null && cacheManager.isDataValid()) {
            // Önbellekte geçerli veri var, bu veriyi kullan
            val currencyResponse = Gson().fromJson(cachedData.toString(), CurrencyResponse::class.java)
            updateUI(currencyResponse.rates)
            showSnackbar("Veriler önbellekten geldi.")
        } else {
            // Önbellekte geçerli veri yok veya veri geçerli değil, API'den veri çek
            fetchCurrencyFromAPI()
        }
    }

    private fun fetchCurrencyFromAPI() {
        CurrencyApiClient.service.getCurrencyRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    val currencyResponse = response.body()
                    currencyResponse?.let {
                        updateUI(it.rates)
                        cacheManager.setCachedData(JSONObject(Gson().toJson(it)))
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
