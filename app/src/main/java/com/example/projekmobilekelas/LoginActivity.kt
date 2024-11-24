package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // Inisialisasi komponen dan Firebase Authentication
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Hubungkan komponen UI
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnlogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        // Event tombol login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validasi input
            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password wajib diisi"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Panggil fungsi login
            loginWithFirebaseAuth(email, password)
        }

        // Event untuk berpindah ke SignUpActivity
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginWithFirebaseAuth(email: String, password: String) {
        // Login menggunakan Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Jika login berhasil
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                    // Pindah ke MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
                } else {
                    // Jika login gagal
                    val errorMessage = task.exception?.message ?: "Terjadi kesalahan"
                    Toast.makeText(this, "Login gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Menangani kesalahan secara eksplisit
                Toast.makeText(this, "Kesalahan jaringan: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
