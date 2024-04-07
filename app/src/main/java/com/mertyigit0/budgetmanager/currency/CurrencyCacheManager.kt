package com.mertyigit0.budgetmanager.currency

import org.json.JSONObject

class CurrencyCacheManager {
    private var cachedData: JSONObject? = null
    private var lastFetchTime: Long = 0

    init {
        // Başlangıç zamanını ayarla
        lastFetchTime = System.currentTimeMillis()
    }

    fun getCachedData(): JSONObject? {
        return cachedData
    }

    fun setCachedData(data: JSONObject) {
        cachedData = data
        lastFetchTime = System.currentTimeMillis()
    }

    fun isDataValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return currentTime - lastFetchTime < oneDayInMillis
    }
}
