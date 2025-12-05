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

    @get:PropertyName("location")
    @set:PropertyName("location")
    var Location: String = ""
)
{
    constructor(UserID: String) : this(UserID, "", "", "")
}
