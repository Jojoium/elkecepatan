package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Main_Menu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu2)

        // Initialize buttons
        val btnBMICalculator: Button = findViewById(R.id.btn_bmi_calculator)
        val btnDiseasePrediction: Button = findViewById(R.id.btn_disease_prediction)
        val btnLifeExpectancy: Button = findViewById(R.id.btn_life_expectancy)
        val btnMentalDisorderPrediction: Button = findViewById(R.id.btn_mental_disorder_prediction)
        val btnLogout: Button = findViewById(R.id.btn_logout)

        // Set click listeners
        btnBMICalculator.setOnClickListener { navigateTo(BMICalculatorActivity::class.java) }
        btnDiseasePrediction.setOnClickListener { navigateTo(DiseasePrediction::class.java) }
        btnLifeExpectancy.setOnClickListener { navigateTo(LifeExpectancyCalculator::class.java) }
        btnMentalDisorderPrediction.setOnClickListener { navigateTo(MentalDisorderPrediction::class.java) }
        btnLogout.setOnClickListener { logout() }
    }

    /**
     * Navigate to the specified activity.
     */
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    /**
     * Handles the logout process and navigates to the LoginActivity.
     */
    private fun logout() {
        // Log out logic (e.g., clear shared preferences, navigate to login screen)
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
