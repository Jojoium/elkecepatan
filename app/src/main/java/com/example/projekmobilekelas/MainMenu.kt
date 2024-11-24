package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btnBMICalculator: Button = findViewById(R.id.btn_bmi_calculator)
        val btnDiseasePrediction: Button = findViewById(R.id.btn_disease_prediction)
        val btnLifeExpectancy: Button = findViewById(R.id.btn_life_expectancy)
        val btnMentalDisorderPrediction: Button = findViewById(R.id.btn_mental_disorder_prediction)
        val btnLogout: Button = findViewById(R.id.btn_logout)

        btnBMICalculator.setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }

        btnDiseasePrediction.setOnClickListener {
            val intent = Intent(this, activity_main::class.)
            startActivity(intent)
        }

        btnLifeExpectancy.setOnClickListener {
            val intent = Intent(this, LifeExpectancyCalculatorActivity::class.java)
            startActivity(intent)
        }

        btnMentalDisorderPrediction.setOnClickListener {
            val intent = Intent(this, MentalDisorderPredictionActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            // Log out logic (e.g., clear shared preferences, navigate to login screen)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
