package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

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

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o"

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

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            val birthMonth = monthInput.text.toString()
            val birthDay = dayInput.text.toString()
            val birthYear = yearInput.text.toString()
            val sex = sexSpinner.selectedItem.toString()
            val smokingStatus = smokingSpinner.selectedItem.toString()
            val bmi = bmiSpinner.selectedItem.toString()
            val outlook = outlookSpinner.selectedItem.toString()
            val alcoholConsumption = alcoholSpinner.selectedItem.toString()
            val country = countrySpinner.selectedItem.toString()
            val fitnessLevel = fitnessSpinner.selectedItem.toString()
            val dietRating = dietSpinner.selectedItem.toString()

            // Validate required fields
            if (userInput.isNotEmpty() && birthYear.isNotEmpty() && birthMonth.isNotEmpty() && birthDay.isNotEmpty()) {
                sendChatRequest(
                    userInput, birthYear, birthMonth, birthDay, sex, smokingStatus, bmi,
                    outlook, alcoholConsumption, country, fitnessLevel, dietRating
                )
            } else {
                Toast.makeText(this@MainActivity, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        val gotoActivity2Button: Button = findViewById(R.id.gotoActivity2Button)
        gotoActivity2Button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
    }

    // Send the request to AI1 for feedback and suggestions
    private fun sendChatRequest(
        userInput: String,
        birthYear: String,
        birthMonth: String,
        birthDay: String,
        sex: String,
        smokingStatus: String,
        bmi: String,
        outlook: String,
        alcoholConsumption: String,
        country: String,
        fitnessLevel: String,
        dietRating: String
    ) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                val message = JSONObject().apply {
                    put("role", "user")
                    put("content", "Based on these factors: Date of birth: $birthMonth/$birthDay/$birthYear, Sex: $sex, Smoking: $smokingStatus, BMI: $bmi, Outlook: $outlook, Alcohol: $alcoholConsumption, Country: $country, Fitness: $fitnessLevel, Diet: $dietRating. Activity description: $userInput, please provide feedback and suggestions for a healthier lifestyle.")
                }
                put(message)
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { outputText.text = "Request failed: ${e.message}" }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val choices = jsonResponse.getJSONArray("choices")
                        val reply = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        runOnUiThread { outputText.text = reply }

                        // After AI1 response, make the AI2 request for the age factor
                        sendDescriptionToAI2(userInput)

                    } catch (e: Exception) {
                        runOnUiThread { outputText.text = "Error: ${e.message}" }
                    }
                } else {
                    runOnUiThread { outputText.text = "Response not successful: ${response.code}" }
                }
            }
        })
    }

    // Send the request to AI2 for calculating the age factor
    private fun sendDescriptionToAI2(description: String) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                val message = JSONObject().apply {
                    put("role", "user")
                    put("content", "Based on the following description: '$description', please provide an age factor as a positive or negative number reflecting the user's daily habits and lifestyle.")
                }
                put(message)
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { outputText2.text = "Request to AI2 failed: ${e.message}" }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val choices = jsonResponse.getJSONArray("choices")
                        val ageFactor = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")

                        runOnUiThread { calculateFinalLifeExpectancy(ageFactor) }


                    } catch (e: Exception) {
                        runOnUiThread { outputText2.text = "Error: ${e.message}" }
                    }
                } else {
                    runOnUiThread { outputText2.text = "Response from AI2 not successful: ${response.code}" }
                }
            }
        })
    }

    // Calculate the final life expectancy using the age factor and user's birth year
    private fun calculateFinalLifeExpectancy(ageFactorContent: String) {
        val ageFactor = extractAgeFactor(ageFactorContent)
        val birthYear = yearInput.text.toString().toInt()
        val countryLifeExpectancy = getLifeExpectancyForCountry(countrySpinner.selectedItem.toString())


        var lifestyleImpact = 0


        lifestyleImpact += when (smokingSpinner.selectedItem.toString().toLowerCase()) {
            "yes" -> -10
            "no" -> 5
            else -> 0
        }


        lifestyleImpact += when (alcoholSpinner.selectedItem.toString().toLowerCase()) {
            "excessive" -> -5
            "moderate" -> 2
            "none" -> 5
            else -> 0
        }


        lifestyleImpact += when (fitnessSpinner.selectedItem.toString().toLowerCase()) {
            "low" -> -5
            "medium" -> 0
            "high" -> 5
            else -> 0
        }


        lifestyleImpact += when (dietSpinner.selectedItem.toString().toLowerCase()) {
            "poor" -> -10
            "average" -> 0
            "good" -> 5
            else -> 0
        }


        lifestyleImpact += when (outlookSpinner.selectedItem.toString().toLowerCase()) {
            "positive" -> 5
            "neutral" -> 0
            "negative" -> -5
            else -> 0
        }


        val finalLifeExpectancy = countryLifeExpectancy + ageFactor + lifestyleImpact
        val deathYear = birthYear + finalLifeExpectancy


        runOnUiThread {
            outputText2.text = "Your estimated life expectancy: $finalLifeExpectancy years. \nEstimated year of death: $deathYear"
        }
    }


    private fun getLifeExpectancyForCountry(country: String): Int {

        return when (country) {
            "USA" -> 78
            "Canada" -> 82
            "Japan" -> 84
            "India" -> 69
            "Australia" -> 83
            "Germany" -> 81
            "France" -> 82
            "UK" -> 81
            "Italy" -> 83
            "Spain" -> 83
            "China" -> 77
            "Brazil" -> 75
            "Mexico" -> 75
            "Russia" -> 72
            "South Korea" -> 83
            "Netherlands" -> 82
            "Sweden" -> 84
            "Norway" -> 84
            "Denmark" -> 81
            "Finland" -> 81
            "Singapore" -> 87
            "New Zealand" -> 81
            "Ireland" -> 82
            "Saudi Arabia" -> 75
            "South Africa" -> 64
            "Argentina" -> 76
            "Indonesia" -> 72
            "Thailand" -> 77
            "Philippines" -> 72
            "Vietnam" -> 75
            "Pakistan" -> 67
            "Bangladesh" -> 73
            "Egypt" -> 72
            "Turkey" -> 77
            "Ukraine" -> 72
            "Colombia" -> 77
            "Chile" -> 80

            else -> 75 // Default value
        }
    }


    // Function to extract the numeric age factor from the response string
    private fun extractAgeFactor(response: String): Int {
        // Use regex to find the first number in the response
        val regex = Regex("""-?\d+""") // Matches integers, including negative numbers
        val matchResult = regex.find(response)

        // If a number is found, convert it to an integer; otherwise, return 0
        return matchResult?.value?.toInt() ?: 0
    }
}

