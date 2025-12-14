package com.example.buddyloc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var buttonRegister: Button
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextDisplayName: EditText
    private lateinit var textViewLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister = findViewById(R.id.registerBtn)
        editTextEmail = findViewById(R.id.emailEt)
        editTextPassword = findViewById(R.id.passwordEt)
        editTextDisplayName = findViewById(R.id.displayNameEt)
        textViewLogin = findViewById(R.id.loginTxt)

        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val displayName = editTextDisplayName.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(email, password, {
                // Registration successful
                val currentUser = Firebase.auth.currentUser
                if (currentUser != null) {
                    // Default location 0.0,0.0
                    firestoreViewModel.saveUser(
                        currentUser.uid,
                        displayName,
                        email,
                        0.0,
                        0.0
                    )
                }

                // Navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            })
        }

        textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }
}
