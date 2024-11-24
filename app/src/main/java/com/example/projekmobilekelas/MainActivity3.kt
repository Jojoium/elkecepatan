package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity3 : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView  // New output field for algorithmic result
    private lateinit var sendButton: Button
    private lateinit var welcomeTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    // SeekBars for each parameter
    private lateinit var intensitasSeekBar: SeekBar
    private lateinit var durasiSeekBar: SeekBar
    private lateinit var frekuensiSeekBar: SeekBar
    private lateinit var keseharianSeekBar: SeekBar
    private lateinit var polaNegatifSeekBar: SeekBar
    private lateinit var ketakutanSeekBar: SeekBar
    private lateinit var konsentrasiSeekBar: SeekBar
    private lateinit var kelelahanSeekBar: SeekBar
    private lateinit var tidurSeekBar: SeekBar
    private lateinit var minatSeekBar: SeekBar
    private lateinit var isolasiSeekBar: SeekBar

    private val client = OkHttpClient()
    private val API_URL = "https://api.openai.com/v1/chat/completions"
    private val API_KEY = "sk-proj-2qQUsGh9z8NkZrqokvCkT3BlbkFJu7wpk7D6njbcQ2B6gV8o" // Replace with your actual API Key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // Set layout
        setContentView(R.layout.activity_main3)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        outputText2 = findViewById(R.id.outputText2)  // Initialize second output field
        sendButton = findViewById(R.id.sendButton)
        welcomeTextView = findViewById(R.id.welcomeTextView)

        // Initialize SeekBars
        intensitasSeekBar = findViewById(R.id.intensitasSeekBar)
        durasiSeekBar = findViewById(R.id.durasiSeekBar)
        frekuensiSeekBar = findViewById(R.id.frekuensiSeekBar)
        keseharianSeekBar = findViewById(R.id.keseharianSeekBar)
        polaNegatifSeekBar = findViewById(R.id.polaNegatifSeekBar)
        ketakutanSeekBar = findViewById(R.id.ketakutanSeekBar)
        konsentrasiSeekBar = findViewById(R.id.konsentrasiSeekBar)
        kelelahanSeekBar = findViewById(R.id.kelelahanSeekBar)
        tidurSeekBar = findViewById(R.id.tidurSeekBar)
        minatSeekBar = findViewById(R.id.minatSeekBar)
        isolasiSeekBar = findViewById(R.id.isolasiSeekBar)


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

        welcomeTextView = findViewById(R.id.welcomeTextView)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Fetch user data
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        welcomeTextView.text = "Selamat datang, $username!"
                    } else {
                        welcomeTextView.text = "Selamat datang!"
                        Toast.makeText(this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            welcomeTextView.text = "Selamat datang, Pengguna!"
            Toast.makeText(this, "Pengguna tidak login", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateScores(): Map<String, Int> {
        // Expanded conditions with calculated scores
        val ocdScore = (polaNegatifSeekBar.progress * 3) + (frekuensiSeekBar.progress * 2) + intensitasSeekBar.progress
        val gadScore = (konsentrasiSeekBar.progress * 2) + (keseharianSeekBar.progress * 3) + (durasiSeekBar.progress * 2)
        val ptsdScore = (ketakutanSeekBar.progress * 3) + (isolasiSeekBar.progress * 2) + (frekuensiSeekBar.progress * 2)
        val depressionScore = (keseharianSeekBar.progress * 2) + (kelelahanSeekBar.progress * 3) + minatSeekBar.progress
        val insomniaScore = (tidurSeekBar.progress * 3) + (isolasiSeekBar.progress * 2) + konsentrasiSeekBar.progress

        return mapOf(
            "OCD" to ocdScore,
            "GAD" to gadScore,
            "PTSD" to ptsdScore,
            "Depression" to depressionScore,
            "Insomnia" to insomniaScore
        )
    }

    private fun displayAlgorithmResult(scores: Map<String, Int>) {
        // Get the highest score and determine condition
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100  // Adjust scaling factor

        outputText2.text = "Condition: $condition\nConfidence: ${confidence.toInt()}%"
    }

    private fun sendChatRequest(userInput: String, scores: Map<String, Int>) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val confidence = (highestScore?.value?.toFloat() ?: 0f) / 30 * 100  // Adjust scaling factor

        val userPrompt = """
            User Message: "$userInput"
            Analysis suggests a high likelihood of $condition with a confidence of ${confidence.toInt()}%.
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
                runOnUiThread { outputText.text = "Request failed" }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val reply = JSONObject(responseData).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content")

                    runOnUiThread { outputText.text = reply }
                } else {
                    runOnUiThread { outputText.text = "Response not successful" }
                }
            }
        })
    }
}
