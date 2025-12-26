package com.example.buddyloc

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirestoreViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private var usersListener: ListenerRegistration? = null

    // ✅ SAVE user (DOUBLE latitude & longitude)
    fun saveUser(
        userId: String,
        displayName: String,
        email: String,
        latitude: Double,
        longitude: Double
    ) {
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "latitude" to latitude,
            "longitude" to longitude
        )

        usersCollection.document(userId).set(user)
    }

    // ✅ REAL-TIME listener for MAP
    fun listenToUsers(callback: (List<User>) -> Unit) {
        usersListener?.remove()

        usersListener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                callback(emptyList())
                return@addSnapshotListener
            }

            val users = snapshot.documents.mapNotNull { doc ->
                val displayName = doc.getString("displayName")
                val email = doc.getString("email")
                val latitude = doc.getDouble("latitude")
                val longitude = doc.getDouble("longitude")

                if (latitude != null && longitude != null) {
                    User(
                        userId = doc.id,
                        displayName = displayName,
                        email = email,
                        latitude = latitude,
                        longitude = longitude
                    )
                } else null
            }

            callback(users)
        }
    }

    // ✅ ONE-TIME fetch (if needed)
    fun getAllUsers(callback: (List<User>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull { doc ->
                    val displayName = doc.getString("displayName")
                    val email = doc.getString("email")
                    val latitude = doc.getDouble("latitude")
                    val longitude = doc.getDouble("longitude")

                    if (latitude != null && longitude != null) {
                        User(
                            userId = doc.id,
                            displayName = displayName,
                            email = email,
                            latitude = latitude,
                            longitude = longitude
                        )
                    } else null
                }
                callback(users)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    // ✅ UPDATE location only
    fun updateUserLocation(userId: String, latitude: Double, longitude: Double) {
        if (userId.isEmpty()) return

        usersCollection.document(userId).update(
            mapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        usersListener?.remove()
    }

    fun updateUserProfile(
        userId: String,
        displayName: String,
        latitude: Double,
        longitude: Double
    ) {
        if (userId.isEmpty()) return

        val updates = hashMapOf<String, Any>(
            "displayName" to displayName,
            "latitude" to latitude,
            "longitude" to longitude
        )

        usersCollection.document(userId)
            .update(updates)
    }
}