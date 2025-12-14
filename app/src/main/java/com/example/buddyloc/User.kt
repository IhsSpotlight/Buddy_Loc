package com.example.buddyloc

import com.google.firebase.firestore.PropertyName

data class User(
    val UserID: String = "",

    @get:PropertyName("displayname")
    @set:PropertyName("displayname")
    var DisplayName: String = "",

    @get:PropertyName("email")
    @set:PropertyName("email")
    var Email: String = "",

    @get:PropertyName("latitude")
    @set:PropertyName("latitude")
    var latitude: Double? = null,

    @get:PropertyName("longitude")
    @set:PropertyName("longitude")
    var longitude: Double? = null
) {
    constructor(UserID: String) : this(UserID, "", "", null, null)
}
