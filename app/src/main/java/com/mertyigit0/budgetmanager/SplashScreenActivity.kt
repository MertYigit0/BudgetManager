package com.mertyigit0.budgetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import com.mertyigit0.budgetmanager.MainActivity
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.ui.InfoActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 1000L // 2 saniye

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        // SplashActivity içinde SharedPreferences kullanarak giriş sayısını takip etme
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var entryCount = sharedPreferences.getInt("entryCount", 0)

// Giriş sayısını artırma
        entryCount++
        sharedPreferences.edit().putInt("entryCount", entryCount).apply()


        Handler().postDelayed({
            // Giriş sayısını kontrol ederek yönlendirme yapma
            if (entryCount >= 99) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity'yi kapat
            } else {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity'yi kapat
            }
        }, SPLASH_DISPLAY_LENGTH)
    }
}
