package com.example.projekmobilekelas.utils

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import.android.R

class PredictionActivity : AppCompatActivity() {

    private lateinit var etAge: EditText
    private lateinit var etBmi: EditText
    private lateinit var etMedicalHistory: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var rgStress: RadioGroup
    private lateinit var rgDiet: RadioGroup
    private lateinit var btnCalculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        etAge = findViewById(R.id.et_age)
        etBmi = findViewById(R.id.et_bmi)
        etMedicalHistory = findViewById(R.id.et_medical_history)
        rgGender = findViewById(R.id.rg_gender)
        rgStress = findViewById(R.id.rg_stress)
        rgDiet = findViewById(R.id.rg_diet)
        btnCalculate = findViewById(R.id.btn_calculate)

        btnCalculate.setOnClickListener {
            calculatePrediction()
        }
    }

    private fun calculatePrediction() {
        // Get inputs
        val age = etAge.text.toString()
        val bmi = etBmi.text.toString()
        val medicalHistory = etMedicalHistory.text.toString()

        if (age.isEmpty() || bmi.isEmpty() || medicalHistory.isEmpty()) {
            Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenderId = rgGender.checkedRadioButtonId
        val selectedStressId = rgStress.checkedRadioButtonId
        val selectedDietId = rgDiet.checkedRadioButtonId

        if (selectedGenderId == -1 || selectedStressId == -1 || selectedDietId == -1) {
            Toast.makeText(this, "Harap pilih semua opsi!", Toast.LENGTH_SHORT).show()
            return
        }

        val rbGender = findViewById<RadioButton>(selectedGenderId)
        val rbStress = findViewById<RadioButton>(selectedStressId)
        val rbDiet = findViewById<RadioButton>(selectedDietId)

        val gender = rbGender.text.toString()
        val stress = rbStress.text.toString()
        val diet = rbDiet.text.toString()

        // Example prediction logic (replace this with actual logic)
        val result = """
            Umur: $age
            BMI: $bmi
            Gender: $gender
            Tingkat Stres: $stress
            Pola Makan: $diet
            Riwayat Penyakit: $medicalHistory
        """.trimIndent()

        // Show result
        Toast.makeText(this, result, Toast.LENGTH_LONG).show()
    }
}
