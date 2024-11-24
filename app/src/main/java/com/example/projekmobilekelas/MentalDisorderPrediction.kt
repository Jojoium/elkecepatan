package com.example.projekmobilekelas

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MentalDisorderPrediction {

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o"

    fun calculateScores(
        intensitas: Int,
        durasi: Int,
        frekuensi: Int,
        keseharian: Int,
        polaNegatif: Int,
        ketakutan: Int,
        konsentrasi: Int,
        kelelahan: Int,
        tidur: Int,
        minat: Int,
        isolasi: Int
    ): Map<String, Int> {
        return mapOf(
            "OCD" to (polaNegatif * 3 + frekuensi * 2 + intensitas),
            "GAD" to (konsentrasi * 2 + keseharian * 3 + durasi * 2),
            "PTSD" to (ketakutan * 3 + isolasi * 2 + frekuensi * 2),
            "Depression" to (keseharian * 2 + kelelahan * 3 + minat),
            "Insomnia" to (tidur * 3 + isolasi * 2 + konsentrasi)
        )
    }

    fun getResult(scores: Map<String, Int>): String {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100
        return "Condition: $condition\nConfidence: ${confidence.toInt()}%"
    }

    fun sendChatRequest(
        userInput: String,
        scores: Map<String, Int>,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100

        val userPrompt = """
        Pesan Pengguna: "$userInput"
        Berdasarkan analisis, terdapat kemungkinan tinggi adanya kondisi $condition dengan tingkat kepercayaan sebesar ${confidence.toInt()}%.
        Mohon berikan nama kondisi ini, anjuran atau langkah selanjutnya yang bisa dilakukan, serta berikan alternatif kondisi jika diagnosis utama tidak tersedia dalam analisis.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userPrompt)
                })
            }
            put("messages", messages)
        }

        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val reply = JSONObject(responseData).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")
                    onSuccess(reply)
                } else {
                    onFailure()
                }
            }
        })
    }
}
