package com.example.buddyloc

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel {
    private val firebaseAuth = FirebaseAuth.getInstance()
    fun login(email: String, password: String, OnSuccess: () -> Unit, OnFailure: (String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            OnSuccess()
        } else {
            OnFailure(task.exception?.message ?: "Login Failed")
        }
    }
        fun register(email: String, password: String, OnSuccess: () -> Unit, OnFailure: (String) -> Unit) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        OnSuccess()
                    } else {
                        OnFailure(task.exception?.message ?: "Registration Failed")
                    }
                }
        }
        fun getCurrentUserID(): String {
            return firebaseAuth.currentUser?. uid ?:""
        }
        fun isLoggedin(): Boolean {
            return firebaseAuth.currentUser != null
        }
       fun getCurrentUser(): FirebaseUser? {
            return firebaseAuth.currentUser
        }



    }




}