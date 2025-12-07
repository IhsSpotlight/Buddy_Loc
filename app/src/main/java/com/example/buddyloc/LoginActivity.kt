package com.example.buddyloc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var ViewModel: AuthenticationViewModel
    private lateinit var btnlogin: Button
    private lateinit var btngotoregister: Button
    private lateinit var btngotoforgotpassword: Button
    private lateinit var emailedittext: EditText
    private lateinit var passwordedittext: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnlogin = findViewById(R.id.loginbtn)
        btngotoregister = findViewById(R.id.createacc)
        btngotoforgotpassword = findViewById(R.id.forgetpasswordbtn)
        emailedittext = findViewById(R.id.emailet)
        passwordedittext = findViewById(R.id.passet)


        btnlogin.setOnClickListener {
            val email = emailedittext.text.toString()
            val password = passwordedittext.text.toString()

            ViewModel.login(
                email, password,
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, { errormessage ->
                    Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show()

                })
        }

        btngotoregister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    // Correct
    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}