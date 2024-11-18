package com.example.calculatorapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobilekelas.DiseasePrediction
import com.example.projekmobilekelas.LifeExpectancyCalculator
import com.example.projekmobilekelas.LoginActivity
import com.example.projekmobilekelas.R

class BMICalculatorActivity {

}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigate to Life Expectancy Activity
        findViewById<Button>(R.id.btn_life_expectancy).setOnClickListener {
            val intent = Intent(this, LifeExpectancyCalculator::class.java)
            startActivity(intent)
        }

        // Navigate to BMI Calculator Activity
        findViewById<Button>(R.id.btn_bmi_calculator).setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Disease Prediction Activity
        findViewById<Button>(R.id.btn_disease_prediction).setOnClickListener {
            val intent = Intent(this, DiseasePrediction::class.java)
            startActivity(intent)
        }

        // Log Out (example: navigate to Login Activity)
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
