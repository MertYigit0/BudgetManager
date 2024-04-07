package com.mertyigit0.budgetmanager.currency

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrencyApiClient {
    private const val BASE_URL = "https://open.er-api.com/v6/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: CurrencyService = retrofit.create(CurrencyService::class.java)
}
