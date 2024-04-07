package com.mertyigit0.budgetmanager.currency

import retrofit2.Call
import retrofit2.http.GET

interface CurrencyService {
    @GET("latest")
    fun getCurrencyRates(): Call<CurrencyResponse>
}
