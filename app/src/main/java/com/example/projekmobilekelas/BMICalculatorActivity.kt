package com.example.projekmobilekelas


import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projekmobilekelas.R

class BMICalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        val ageEditText = findViewById<EditText>(R.id.ageEditText)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.genderRadioGroup)
        val heightEditText = findViewById<EditText>(R.id.heightEditText)
        val weightEditText = findViewById<EditText>(R.id.weightEditText)
        val calculateButton = findViewById<Button>(R.id.calculateButton)
        val bmiResultTextView = findViewById<TextView>(R.id.bmiResultTextView)
        val classResultTextView = findViewById<TextView>(R.id.classResultTextView)

        calculateButton.setOnClickListener {
            val age = ageEditText.text.toString().toIntOrNull() ?: 0
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId == R.id.maleRadioButton) "Laki-Laki" else "Perempuan"
            val height = heightEditText.text.toString().toDoubleOrNull() ?: 0.0
            val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (height > 0 && weight > 0) {
                val bmi = calculateBMI(height, weight)
                bmiResultTextView.text = String.format("%.1f", bmi)
                classResultTextView.text = getBMIClassification(bmi)
            } else {
                Toast.makeText(this, "Please enter valid height and weight", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateBMI(height: Double, weight: Double): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }

    private fun getBMIClassification(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "At risk of overweight"
            bmi < 34.9 -> "Obesity class I"
            bmi < 39.9 -> "Obesity class II"
            else -> "Obesity class III"
        }
    }
}
