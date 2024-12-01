package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView
    private lateinit var sendButton: Button
    private lateinit var monthInput: EditText
    private lateinit var dayInput: EditText
    private lateinit var yearInput: EditText
    private lateinit var sexSpinner: Spinner
    private lateinit var smokingSpinner: Spinner
    private lateinit var bmiSpinner: Spinner
    private lateinit var outlookSpinner: Spinner
    private lateinit var alcoholSpinner: Spinner
    private lateinit var countrySpinner: Spinner
    private lateinit var fitnessSpinner: Spinner
    private lateinit var dietSpinner: Spinner

    private lateinit var calculator: LifeExpectancyCalculator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        outputText2 = findViewById(R.id.outputText2)
        sendButton = findViewById(R.id.sendButton)
        monthInput = findViewById(R.id.monthInput)
        dayInput = findViewById(R.id.dayInput)
        yearInput = findViewById(R.id.yearInput)
        sexSpinner = findViewById(R.id.sexSpinner)
        smokingSpinner = findViewById(R.id.smokingSpinner)
        bmiSpinner = findViewById(R.id.bmiSpinner)
        outlookSpinner = findViewById(R.id.outlookSpinner)
        alcoholSpinner = findViewById(R.id.alcoholSpinner)
        countrySpinner = findViewById(R.id.countrySpinner)
        fitnessSpinner = findViewById(R.id.fitnessSpinner)
        dietSpinner = findViewById(R.id.dietSpinner)

        calculator = LifeExpectancyCalculator()

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            val birthYear = yearInput.text.toString()
            val birthMonth = monthInput.text.toString()
            val birthDay = dayInput.text.toString()
            val sex = sexSpinner.selectedItem.toString()
            val smoking = smokingSpinner.selectedItem.toString()
            val bmi = bmiSpinner.selectedItem.toString()
            val outlook = outlookSpinner.selectedItem.toString()
            val alcohol = alcoholSpinner.selectedItem.toString()
            val country = countrySpinner.selectedItem.toString()
            val fitness = fitnessSpinner.selectedItem.toString()
            val diet = dietSpinner.selectedItem.toString()

            if (userInput.isNotEmpty() && birthYear.isNotEmpty() && birthMonth.isNotEmpty() && birthDay.isNotEmpty()) {
                calculator.calculateLifeExpectancy(
                    userInput, birthYear, birthMonth, birthDay, sex, smoking, bmi,
                    outlook, alcohol, country, fitness, diet,
                    this
                )
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

//        val gotoActivity2Button: Button = findViewById(R.id.gotoActivity2Button)
//        gotoActivity2Button.setOnClickListener {
//            val intent = Intent(this, MainActivity2::class.java)
//            startActivity(intent)
//        }
    }

    fun updateOutputText(content: String) {
        outputText.text = content
    }

    fun updateOutputText2(content: String) {
        outputText2.text = content
    }
}
