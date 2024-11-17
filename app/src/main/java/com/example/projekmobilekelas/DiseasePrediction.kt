package com.example.projekmobilekelas

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class DiseasePrediction {

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o"

    fun calculateScores(
        fatigue: Int,
        pain: Int,
        nausea: Int,
        breath: Int,
        dizziness: Int,
        appetite: Int,
        weakness: Int
    ): Map<String, Int> {
        val fatigueScore = fatigue * 3
        val painScore = pain * 2
        val nauseaScore = nausea * 2
        val breathScore = breath * 3
        val dizzinessScore = dizziness * 2
        val appetiteScore = appetite * 2
        val weaknessScore = weakness * 3

        val anemiaScore = (fatigue * 3) + (weakness * 2) + (dizziness * 2)
        val hypertensionScore = (dizziness * 3) + (breath * 2) + (pain * 2)
        val asthmaScore = (breath * 3) + (fatigue * 2)
        val chronicKidneyDiseaseScore = (fatigue * 3) + (nausea * 2) + (appetite * 2)
        val chronicLiverDiseaseScore = (appetite * 3) + (fatigue * 2) + (weakness * 2)

        return mapOf(
            "Chronic Fatigue" to fatigueScore,
            "Pain Disorder" to painScore,
            "Digestive Issue" to nauseaScore,
            "Respiratory Issue" to breathScore,
            "Circulatory Issue" to dizzinessScore,
            "Appetite Loss" to appetiteScore,
            "Muscle Weakness" to weaknessScore,
            "Anemia" to anemiaScore,
            "Hypertension" to hypertensionScore,
            "Asthma" to asthmaScore,
            "Chronic Kidney Disease" to chronicKidneyDiseaseScore,
            "Chronic Liver Disease" to chronicLiverDiseaseScore
        )
    }

    fun sendChatRequest(
        input: String,
        scores: Map<String, Int>,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100

        val userPrompt = """
        Pesan Pengguna: "$input"
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
                onFailure("Permintaan gagal: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val reply = JSONObject(responseData).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")
                    onSuccess(reply)
                } else {
                    onFailure("Respon tidak berhasil: ${response.message}")
                }
            }
        })
    }
}
