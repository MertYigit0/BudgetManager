package com.mertyigit0.budgetmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mertyigit0.budgetmanager.ui.Info1Fragment
import com.mertyigit0.budgetmanager.ui.Info2Fragment

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.incomeFragment -> {
                    // Navigate to the default position
                    navController.navigate(R.id.incomeFragment)
                    true
                }

                R.id.expenseFragment -> {
                    navController.navigate(R.id.expenseFragment)
                    true
                }
                R.id.budgetAlertFragment -> {
                    navController.navigate(R.id.budgetAlertFragment)
                    true
                }

                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                R.id.financialGoalFragment-> {
                    navController.navigate(R.id.financialGoalFragment)
                    true
                }
                //
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }
        }



    }
}
