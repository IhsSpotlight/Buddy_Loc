package com.example.buddyloc

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.net.PasswordAuthentication

class RegisterActivity : AppCompatActivity() {
    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextDisplayName: EditText
    private lateinit var editTextConPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var textViewLogin: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        buttonRegister = findViewById(R.id.Registerbtn)
        editTextEmail = findViewById(R.id.emailet)
        editTextPassword = findViewById(R.id.passet)
        editTextDisplayName = findViewById(R.id.emailet0)
        editTextConPassword = findViewById(R.id.conpasset)
        textViewLogin = findViewById(R.id.alreadyhaveacc)

        authViewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel::class.java)

        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.toString()

            authViewModel.register(email, password,
                {
                    val displayName = editTextDisplayName.text.toString()
                    val location = "Location not available"

                    firestoreViewModel.SaveUser(authViewModel.getCurrentUserID(),
                        displayName,
                        email,
                        location
                    )
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },{errormessage ->
                    Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show()
                })
        }
        textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }


    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser!=null)
        {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
    }
    }

}

