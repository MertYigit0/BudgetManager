package com.mertyigit0.budgetmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mertyigit0.budgetmanager.MainActivity
import com.mertyigit0.budgetmanager.R
import com.mertyigit0.budgetmanager.ui.InfoActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 1000L // 2 saniye
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = Firebase.auth
        // SplashActivity içinde SharedPreferences kullanarak giriş sayısını takip etme
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var entryCount = sharedPreferences.getInt("entryCount", 0)

// Giriş sayısını artırma
        entryCount++
        sharedPreferences.edit().putInt("entryCount", entryCount).apply()


        Handler().postDelayed({
            // Giriş sayısını kontrol ederek yönlendirme yapma

            val currentUser = FirebaseAuth.getInstance().currentUser



                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity'yi kapat





        }, SPLASH_DISPLAY_LENGTH)
    }
}
