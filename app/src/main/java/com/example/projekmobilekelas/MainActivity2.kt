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

class MainActivity2 : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView
    private lateinit var sendButton: Button

    // SeekBars for each physical symptom
    private lateinit var fatigueSeekBar: SeekBar
    private lateinit var painSeekBar: SeekBar
    private lateinit var nauseaSeekBar: SeekBar
    private lateinit var breathSeekBar: SeekBar
    private lateinit var dizzinessSeekBar: SeekBar
    private lateinit var appetiteSeekBar: SeekBar
    private lateinit var weaknessSeekBar: SeekBar

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        outputText2 = findViewById(R.id.outputText2)
        sendButton = findViewById(R.id.sendButton)

        // Initialize SeekBars
        fatigueSeekBar = findViewById(R.id.fatigueSeekBar)
        painSeekBar = findViewById(R.id.painSeekBar)
        nauseaSeekBar = findViewById(R.id.nauseaSeekBar)
        breathSeekBar = findViewById(R.id.breathSeekBar)
        dizzinessSeekBar = findViewById(R.id.dizzinessSeekBar)
        appetiteSeekBar = findViewById(R.id.appetiteSeekBar)
        weaknessSeekBar = findViewById(R.id.weaknessSeekBar)

        sendButton.setOnClickListener {
            val userInput = inputText.text.toString()
            if (userInput.isNotEmpty()) {
                val scores = calculateScores()
                displayAlgorithmResult(scores)
                sendChatRequest(userInput, scores)
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }

        val gotoActivity3Button: Button = findViewById(R.id.gotoActivity3Button)
        gotoActivity3Button.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }


    }

    private fun calculateScores(): Map<String, Int> {
        val fatigueScore = fatigueSeekBar.progress * 3
        val painScore = painSeekBar.progress * 2
        val nauseaScore = nauseaSeekBar.progress * 2
        val breathScore = breathSeekBar.progress * 3
        val dizzinessScore = dizzinessSeekBar.progress * 2
        val appetiteScore = appetiteSeekBar.progress * 2
        val weaknessScore = weaknessSeekBar.progress * 3

        val anemiaScore = (fatigueSeekBar.progress * 3) + (weaknessSeekBar.progress * 2) + (dizzinessSeekBar.progress * 2)
        val hypertensionScore = (dizzinessSeekBar.progress * 3) + (breathSeekBar.progress * 2) + (painSeekBar.progress * 2)
        val asthmaScore = (breathSeekBar.progress * 3) + (fatigueSeekBar.progress * 2)
        val chronicKidneyDiseaseScore = (fatigueSeekBar.progress * 3) + (nauseaSeekBar.progress * 2) + (appetiteSeekBar.progress * 2)
        val chronicLiverDiseaseScore = (appetiteSeekBar.progress * 3) + (fatigueSeekBar.progress * 2) + (weaknessSeekBar.progress * 2)

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


    private fun displayAlgorithmResult(scores: Map<String, Int>) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val score = highestScore?.value
        val confidence = (score?.toFloat() ?: 0f) / 30 * 100

        outputText2.text = "Kemungkinan kondisi: $condition\nTingkat Kepercayaan: ${confidence.toInt()}%"
    }


    private fun sendChatRequest(input: String, scores: Map<String, Int>) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100

        val userPrompt = """
        Pesan Pengguna: "$input"
        Berdasarkan analisis, terdapat kemungkinan tinggi adanya kondisi $condition dengan tingkat kepercayaan sebesar ${confidence.toInt()}%.
        Mohon berikan nama kondisi ini, anjuran atau langkah selanjutnya yang bisa dilakukan, serta berikan alternatif kondisi jika diagnosis utama tidak tersedia dalam analisis.
    """

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
                runOnUiThread { outputText.text = "Permintaan gagal" }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val reply = JSONObject(responseData).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")

                    runOnUiThread { outputText.text = reply }
                } else {
                    runOnUiThread { outputText.text = "Respon tidak berhasil" }
                }
            }
        })
    }

}
