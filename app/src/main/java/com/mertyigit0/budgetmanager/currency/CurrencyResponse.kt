package com.mertyigit0.budgetmanager.currency

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("result") val result: String,
    @SerializedName("rates") val rates: Map<String, Double>
)
