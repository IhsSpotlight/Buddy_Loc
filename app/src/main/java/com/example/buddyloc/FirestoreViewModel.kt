package com.example.buddyloc

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // Save user with location as "lat,lon" string
    fun saveUser(userId: String, displayName: String, email: String, latitude: Double, longitude: Double) {
        val location = "$latitude,$longitude"
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "location" to location
        )

        usersCollection.document(userId).set(user)
            .addOnSuccessListener { /* User saved */ }
            .addOnFailureListener { e -> /* Handle failure */ }
    }

    // Get all users and parse location into latitude & longitude
    fun getAllUsers(callback: (List<User>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<User>()
                for (document in result) {
                    val userId = document.id
                    val displayName = document.getString("displayName") ?: ""
                    val email = document.getString("email") ?: ""
                    val loc = document.getString("location") ?: "0,0"
                    val parts = loc.split(",")
                    val latitude = parts.getOrNull(0)?.toDoubleOrNull()
                    val longitude = parts.getOrNull(1)?.toDoubleOrNull()
                    userList.add(User(userId, displayName, email, latitude, longitude))
                }
                callback(userList)
            }
            .addOnFailureListener { e -> callback(emptyList()) }
    }

    // Update user display name and location
    fun updateUser(userId: String, displayName: String, latitude: Double, longitude: Double) {
        val location = "$latitude,$longitude"
        val userMap = mapOf(
            "displayName" to displayName,
            "location" to location
        )
        usersCollection.document(userId).update(userMap)
            .addOnSuccessListener { /* User updated */ }
            .addOnFailureListener { e -> /* Handle failure */ }
    }

    // Update only location
    fun updateUserLocation(userId: String, latitude: Double, longitude: Double) {
        if (userId.isEmpty()) return
        val location = "$latitude,$longitude"
        usersCollection.document(userId).update("location", location)
            .addOnSuccessListener { /* Location updated */ }
            .addOnFailureListener { e -> /* Handle failure */ }
    }

    // Get single user
    fun getUser(userId: String, callback: (User?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val userId = documentSnapshot.id
                val displayName = documentSnapshot.getString("displayName") ?: ""
                val email = documentSnapshot.getString("email") ?: ""
                val loc = documentSnapshot.getString("location") ?: "0,0"
                val parts = loc.split(",")
                val latitude = parts.getOrNull(0)?.toDoubleOrNull()
                val longitude = parts.getOrNull(1)?.toDoubleOrNull()
                val user = User(userId, displayName, email, latitude, longitude)
                callback(user)
            }
            .addOnFailureListener { callback(null) }
    }

    // Get user location as Pair<Double, Double>
    fun getUserLocation(userId: String, callback: (Pair<Double, Double>?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val loc = documentSnapshot.getString("location") ?: return@addOnSuccessListener callback(null)
                val parts = loc.split(",")
                val lat = parts.getOrNull(0)?.toDoubleOrNull()
                val lon = parts.getOrNull(1)?.toDoubleOrNull()
                if (lat != null && lon != null) callback(Pair(lat, lon)) else callback(null)
            }
            .addOnFailureListener { callback(null) }
    }
}
