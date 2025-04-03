// User.kt
package com.example.hs_kart.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String = ""  // For storing profile image URL
) {
    // Empty constructor for Firebase
    constructor() : this("", "", "", "", "")
}