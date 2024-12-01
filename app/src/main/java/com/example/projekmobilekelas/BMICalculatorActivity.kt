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
            val age = ageEditText.text.toString().toIntOrNull()
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val gender = when (selectedGenderId) {
                R.id.maleRadioButton -> "Laki-laki"
                R.id.femaleRadioButton -> "Perempuan"
                else -> null
            }
            val height = heightEditText.text.toString().toDoubleOrNull()
            val weight = weightEditText.text.toString().toDoubleOrNull()

            // Validasi input
            if (age == null || age <= 0) {
                Toast.makeText(this, "Harap masukkan usia yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (gender == null) {
                Toast.makeText(this, "Harap pilih jenis kelamin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (height == null || height <= 0 || weight == null || weight <= 0) {
                Toast.makeText(this, "Harap masukkan tinggi dan berat yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perhitungan BMI
            val bmi = calculateBMI(height, weight)
            val classification = getBMIClassification(bmi, age, gender)

            // Menampilkan hasil
            bmiResultTextView.text = String.format("BMI Anda: %.1f", bmi)
            classResultTextView.text = classification
        }
    }

    private fun calculateBMI(height: Double, weight: Double): Double {
        val heightInMeters = height / 100
        return weight / (heightInMeters * heightInMeters)
    }

    private fun getBMIClassification(bmi: Double, age: Int, gender: String): String {
        val baseClassification = when {
            bmi < 18.5 -> "Kekurangan berat badan"
            bmi < 24.9 -> "Berat badan normal"
            bmi < 29.9 -> "Berisiko kelebihan berat badan"
            bmi < 34.9 -> "Obesitas kelas I"
            bmi < 39.9 -> "Obesitas kelas II"
            else -> "Obesitas kelas III"
        }

        // Penyesuaian berdasarkan usia atau gender (opsional)
        val ageAdjustment = when {
            age < 18 -> " (Penyesuaian untuk remaja)"
            age > 65 -> " (Penyesuaian untuk lansia)"
            else -> ""
        }

        val genderAdjustment = if (gender == "Perempuan" && bmi > 25) {
            " (Faktor hormonal perlu diperhatikan)"
        } else {
            ""
        }

        return "$baseClassification$ageAdjustment$genderAdjustment"
    }
}
