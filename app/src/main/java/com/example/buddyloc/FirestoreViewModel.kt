package com.example.buddyloc

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class FirestoreViewModel: ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun SaveUser(UserID: String, DisplayName: String, Email: String, Location: String) {
        val user = hashMapOf(
            "UserID" to UserID,
            "DisplayName" to DisplayName,
            "Email" to Email,
            "Location" to Location
        )
        usersCollection.document(UserID).set(user)
            .addOnSuccessListener {
                println("User saved successfully")
            }
            .addOnFailureListener { e ->
                println("Error saving user: $e")
            }
        //Here we are Getting the collectiosn from  the Firestore Database
        fun getAllUsers(callback: (List<User>) -> Unit) {
            usersCollection.get()
                .addOnSuccessListener { result ->
                    val userList = mutableListOf<User>()
                    for (document in result) {
                        val userId = document.id
                        val displayName = document.getString("DisplayName") ?: ""
                        val email = document.getString("Email") ?: ""
                        val location = document.getString("Location") ?: ""
                        val user = User(userId, displayName, email, location)
                        userList.add(user)
                    }
                    callback(userList)
                }
                .addOnFailureListener { e ->
                    println("Error getting users: $e")
                }

        }

        fun updateUser(UserID: String, DisplayName: String, Email: String, Location: String) {
            val user = hashMapOf(
                "DisplayName" to DisplayName,
                "Location" to Location
            )
            val userMap = user.toMap()
            usersCollection.document(UserID).update(userMap)
                .addOnSuccessListener { }
                .addOnFailureListener { e ->
                    println("Error updating user: $e")
                }
        }

        fun updateUserLocation(UserID: String, Location: String) {
            if (UserID.isEmpty()) {
                return
            }
            val user = hashMapOf(
                "Location" to Location
            )
            val userMap = user.toMap()
            usersCollection.document(UserID).update(userMap)
                .addOnSuccessListener { }
                .addOnFailureListener { e ->
                    println("Error updating user: $e")
                }

        }

        fun getUser(UserID: String, callback: (User?) -> Unit) {
            usersCollection.document(UserID).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    callback(user)
                }
                .addOnFailureListener { e ->
                    println("Error getting user: $e")
                    callback(null) // Return null in case of failure
                }

        }
        fun getUserLocation(UserID: String, callback: (String?) -> Unit) {
            usersCollection.document(UserID).get()
                .addOnSuccessListener { documentSnapshot ->
                    val location = documentSnapshot.getString("Location")
                    callback(location)
                }
                .addOnFailureListener { e ->
                    println("Error getting user location: $e")
                    callback(null) // Return null in case of failure
                }
        }

    }
}

