package com.example.buddyloc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var btnlogin: Button
    private lateinit var btngotoregister: Button
    private lateinit var emailedittext: EditText
    private lateinit var passwordedittext: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize ViewModel here
        viewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]

        btnlogin = findViewById(R.id.loginbtn)
        btngotoregister = findViewById(R.id.createacc)
        emailedittext = findViewById(R.id.emailet)
        passwordedittext = findViewById(R.id.passet)

        btnlogin.setOnClickListener {
            val email = emailedittext.text.toString()
            val password = passwordedittext.text.toString()

            viewModel.login(
                email, password,
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, { errormessage ->
                    Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show()
                }
            )
        }

        btngotoregister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
