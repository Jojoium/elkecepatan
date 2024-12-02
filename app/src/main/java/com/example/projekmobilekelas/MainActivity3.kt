package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3 : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var outputText: TextView
    private lateinit var outputText2: TextView
    private lateinit var sendButton: Button
    private lateinit var welcomeTextView: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // SeekBars
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

    private val mentalDisorderPrediction = MentalDisorderPrediction()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // Initialize UI components
        inputText = findViewById(R.id.inputText)
        outputText = findViewById(R.id.outputText)
        outputText2 = findViewById(R.id.outputText2)
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
                val scores = mentalDisorderPrediction.calculateScores(
                    intensitasSeekBar.progress,
                    durasiSeekBar.progress,
                    frekuensiSeekBar.progress,
                    keseharianSeekBar.progress,
                    polaNegatifSeekBar.progress,
                    ketakutanSeekBar.progress,
                    konsentrasiSeekBar.progress,
                    kelelahanSeekBar.progress,
                    tidurSeekBar.progress,
                    minatSeekBar.progress,
                    isolasiSeekBar.progress
                )
                val result = mentalDisorderPrediction.getResult(scores)
                outputText2.text = result

                mentalDisorderPrediction.sendChatRequest(
                    userInput,
                    scores,
                    onSuccess = { reply -> runOnUiThread { outputText.text = reply } },
                    onFailure = { runOnUiThread { outputText.text = "Request failed" } }
                )
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
            }
        }

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
                    R.id.prediksi_penyakit_fisik -> {
                        val intent = Intent(this, MainActivity2::class.java)
                        startActivity(intent)
                        finish() // Finish current activity to avoid overlapping
                    }
                    else -> false
                }

            }
            true
        }

        // Highlight kalkulator_umur
        bottomNavigation.selectedItemId = R.id.prediksi_penyakit_mental
    }
}
