package com.mertyigit0.budgetmanager.currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mertyigit0.budgetmanager.R
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
        CurrencyApiClient.service.getCurrencyRates().enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                if (response.isSuccessful) {
                    val currencyResponse = response.body()
                    currencyResponse?.let {
                        adapter = CurrencyListAdapter(it.rates)
                        recyclerView.adapter = adapter
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
}
