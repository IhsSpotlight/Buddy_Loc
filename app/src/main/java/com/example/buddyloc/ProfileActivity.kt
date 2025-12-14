package com.example.buddyloc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthenticationViewModel
    private lateinit var firestoreViewModel: FirestoreViewModel
    private lateinit var buttonUpdateProfile: Button
    private lateinit var textViewEmail: EditText
    private lateinit var editTextNewLocation: EditText
    private lateinit var editTextNewName: EditText
    private lateinit var logoutBtn: ImageButton
    private lateinit var homeBtn: ImageButton
    private val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        buttonUpdateProfile = findViewById(R.id.button)
        homeBtn = findViewById(R.id.homebtn)
        logoutBtn = findViewById(R.id.logoutbtn)
        textViewEmail = findViewById(R.id.emailEt)
        editTextNewLocation = findViewById(R.id.locationBtn)
        editTextNewName = findViewById(R.id.textinputlayout5)

        // Initialize ViewModels
        authViewModel = ViewModelProvider(this)[AuthenticationViewModel::class.java]
        firestoreViewModel = ViewModelProvider(this)[FirestoreViewModel::class.java]

        // Button listeners
        logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Load user info
        loadUserInfo()

        // Update profile
        buttonUpdateProfile.setOnClickListener {
            val newName = editTextNewName.text.toString().trim()
            val newLocation = editTextNewLocation.text.toString().trim()

            if (newName.isEmpty() || newLocation.isEmpty()) {
                Toast.makeText(this, "Name and location cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                updateProfile(newName, newLocation)
            }
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        textViewEmail.setText(currentUser.email)

        firestoreViewModel.getUser(currentUser.uid) { user ->
            if (user != null) {
                editTextNewName.setText(user.DisplayName)

                // Parse location string if available
                val location = user.latitude?.let { lat ->
                    user.longitude?.let { lon ->
                        "$lat,$lon"
                    }
                } ?: ""
                editTextNewLocation.setText(location)
            } else {
                Toast.makeText(this, "User not found in Firestore", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile(newName: String, newLocation: String) {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        // If location is "lat,lon" string, split it
        val parts = newLocation.split(",")
        if (parts.size != 2) {
            Toast.makeText(this, "Location must be in 'lat,lon' format", Toast.LENGTH_SHORT).show()
            return
        }

        val latitude = parts[0].toDoubleOrNull()
        val longitude = parts[1].toDoubleOrNull()
        if (latitude == null || longitude == null) {
            Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show()
            return
        }

        firestoreViewModel.updateUser(userId, newName, latitude, longitude)
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
    }
}
