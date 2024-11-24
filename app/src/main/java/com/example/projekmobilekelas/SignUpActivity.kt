package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnDaftar: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Inisialisasi Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val tvSignIn = findViewById<TextView>(R.id.tvSignIn)
        btnDaftar = findViewById(R.id.btndaftar)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etUsername = findViewById(R.id.etUsername)

        tvSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnDaftar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val username = etUsername.text.toString().trim()

            // Validasi input
            if (username.isEmpty()) {
                etUsername.error = "Username harus diisi"
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                etEmail.error = "Email harus diisi"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email tidak valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                etPassword.error = "Password harus lebih dari 6 karakter"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // Proses pendaftaran
            registerUser(username, email, password)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        // Daftarkan pengguna di Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Dapatkan UID pengguna
                    val uid = auth.currentUser?.uid

                    if (uid != null) {
                        // Simpan data pengguna di Firestore tanpa password
                        val user = hashMapOf(
                            "username" to username,
                            "email" to email
                        )

                        firestore.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal menyimpan data pengguna: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Pendaftaran gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
