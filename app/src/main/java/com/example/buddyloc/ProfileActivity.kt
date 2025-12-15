package com.example.buddyloc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel

    private lateinit var updateBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var nameEt: EditText
    private lateinit var logoutBtn: ImageButton
    private lateinit var homeBtn: ImageButton

    private val firebaseAuth = FirebaseAuth.getInstance()

    // ✅ Current location (should be set via GPS)
    private var currentLat: Double = 0.0
    private var currentLng: Double = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        updateBtn = findViewById(R.id.button)
        homeBtn = findViewById(R.id.homebtn)
        logoutBtn = findViewById(R.id.logoutbtn)
        emailEt = findViewById(R.id.emailTXT)
        nameEt = findViewById(R.id.displaynameTXT)

        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        loadUserInfo()

        logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        updateBtn.setOnClickListener {
            val newName = nameEt.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(newName)
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser() ?: return

        emailEt.setText(currentUser.email)

        firestoreViewModel.listenToUsers { users ->
            val user = users.find { it.userId == currentUser.uid } ?: return@listenToUsers

            nameEt.setText(user.displayName)
            currentLat = user.latitude ?: 0.0
            currentLng = user.longitude ?: 0.0
        }
    }

    private fun updateProfile(newName: String) {
        val currentUser = authViewModel.getCurrentUser() ?: return

        // ⚠ Replace this with GPS update
        if (currentLat == 0.0 || currentLng == 0.0) {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            return
        }

        firestoreViewModel.updateUserLocation(
            currentUser.uid,
            currentLat,
            currentLng
        )

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
    }
}
