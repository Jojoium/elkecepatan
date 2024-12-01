package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView
    private lateinit var sendButton: Button
    private lateinit var welcomeTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    // SeekBars for each physical symptom
    private lateinit var fatigueSeekBar: SeekBar
    private lateinit var painSeekBar: SeekBar
    private lateinit var nauseaSeekBar: SeekBar
    private lateinit var breathSeekBar: SeekBar
    private lateinit var dizzinessSeekBar: SeekBar
    private lateinit var appetiteSeekBar: SeekBar
    private lateinit var weaknessSeekBar: SeekBar

    private val diseasePrediction = DiseasePrediction()

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
                val scores = diseasePrediction.calculateScores(
                    fatigueSeekBar.progress,
                    painSeekBar.progress,
                    nauseaSeekBar.progress,
                    breathSeekBar.progress,
                    dizzinessSeekBar.progress,
                    appetiteSeekBar.progress,
                    weaknessSeekBar.progress
                )
                displayAlgorithmResult(scores)
                diseasePrediction.sendChatRequest(
                    userInput,
                    scores,
                    onSuccess = { reply ->
                        runOnUiThread { outputText.text = reply }
                    },
                    onFailure = { error ->
                        runOnUiThread { outputText.text = error }
                    }
                )
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }

//        val gotoActivity3Button: Button = findViewById(R.id.gotoActivity3Button)
//        gotoActivity3Button.setOnClickListener {
//            val intent = Intent(this, MainActivity3::class.java)
//            startActivity(intent)
//        }
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

        // Set up BottomNavigationView
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            if (bottomNavigation.selectedItemId != item.itemId) {
                when (item.itemId) {
                    R.id.kalkulator_umur -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    R.id.kalkulator_bmi -> {
                        startActivity(Intent(this, BMICalculatorActivity::class.java))
                        finish()
                    }

                    R.id.prediksi_penyakit_mental -> {
                        startActivity(Intent(this, MainActivity3::class.java))
                        finish()
                    }
                }

            }
            true
        }
    }

    private fun displayAlgorithmResult(scores: Map<String, Int>) {
        val highestScore = scores.maxByOrNull { it.value }
        val condition = highestScore?.key
        val score = highestScore?.value
        val confidence = (score?.toFloat() ?: 0f) / 30 * 100

        outputText2.text = "Kemungkinan kondisi: $condition\nTingkat Kepercayaan: ${confidence.toInt()}%"
    }


}
