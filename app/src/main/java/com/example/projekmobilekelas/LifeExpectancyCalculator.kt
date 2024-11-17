package com.example.projekmobilekelas

import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class LifeExpectancyCalculator {

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o"

    fun calculateLifeExpectancy(
        userInput: String,
        birthYear: String,
        birthMonth: String,
        birthDay: String,
        sex: String,
        smoking: String,
        bmi: String,
        outlook: String,
        alcohol: String,
        country: String,
        fitness: String,
        diet: String,
        activity: MainActivity
    ) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                val message = JSONObject().apply {
                    put(
                        "role", "user"
                    )
                    put(
                        "content",
                        "Based on these factors: Date of birth: $birthMonth/$birthDay/$birthYear, Sex: $sex, Smoking: $smoking, BMI: $bmi, Outlook: $outlook, Alcohol: $alcohol, Country: $country, Fitness: $fitness, Diet: $diet. Activity description: $userInput, please provide feedback and suggestions for a healthier lifestyle."
                    )
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
                activity.runOnUiThread { activity.updateOutputText("Request failed: ${e.message}") }
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

                        activity.runOnUiThread { activity.updateOutputText(reply) }

                        // After getting response, calculate final life expectancy
                        calculateFinalLifeExpectancy(
                            userInput, birthYear.toInt(), country, smoking, alcohol, fitness, diet, outlook, activity
                        )

                    } catch (e: Exception) {
                        activity.runOnUiThread { activity.updateOutputText("Error: ${e.message}") }
                    }
                } else {
                    activity.runOnUiThread {
                        activity.updateOutputText("Response not successful: ${response.code}")
                    }
                }
            }
        })
    }

    private fun calculateFinalLifeExpectancy(
        userInput: String,
        birthYear: Int,
        country: String,
        smoking: String,
        alcohol: String,
        fitness: String,
        diet: String,
        outlook: String,
        activity: MainActivity
    ) {
        val countryLifeExpectancy = getLifeExpectancyForCountry(country)
        var lifestyleImpact = 0

        lifestyleImpact += when (smoking.lowercase()) {
            "yes" -> -10
            "no" -> 5
            else -> 0
        }

        lifestyleImpact += when (alcohol.lowercase()) {
            "excessive" -> -5
            "moderate" -> 2
            "none" -> 5
            else -> 0
        }

        lifestyleImpact += when (fitness.lowercase()) {
            "low" -> -5
            "medium" -> 0
            "high" -> 5
            else -> 0
        }

        lifestyleImpact += when (diet.lowercase()) {
            "poor" -> -10
            "average" -> 0
            "good" -> 5
            else -> 0
        }

        lifestyleImpact += when (outlook.lowercase()) {
            "positive" -> 5
            "neutral" -> 0
            "negative" -> -5
            else -> 0
        }

        val finalLifeExpectancy = countryLifeExpectancy + lifestyleImpact
        val deathYear = birthYear + finalLifeExpectancy

        activity.runOnUiThread {
            activity.updateOutputText2("Your estimated life expectancy: $finalLifeExpectancy years. \nEstimated year of death: $deathYear")
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
}
