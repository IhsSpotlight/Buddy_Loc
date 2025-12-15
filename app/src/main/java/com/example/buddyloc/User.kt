package com.example.buddyloc

data class User(
    val userId: String = "",

    var displayName: String? = "",
    var email: String? = "",

    var latitude: Double? = null,
    var longitude: Double? = null
)
