    package com.example.projekmobilekelas

    import android.content.Intent
    import android.os.Bundle
    import android.util.Patterns
    import android.widget.Button
    import android.widget.EditText
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import com.google.firebase.firestore.FirebaseFirestore

    class SignUpActivity : AppCompatActivity() {
        private lateinit var firestore: FirebaseFirestore
        private lateinit var btnDaftar: Button
        private lateinit var etEmail: EditText
        private lateinit var etPassword: EditText
        private lateinit var etUsername: EditText

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_sign_up)

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            firestore = FirebaseFirestore.getInstance()
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

                saveUserData(username, email, password)
            }
        }

        private fun saveUserData(username: String, email: String, password: String) {
            val userId = firestore.collection("users").document().id
            val user = hashMapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )

            firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Pendaftaran gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
