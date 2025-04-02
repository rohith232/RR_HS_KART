package com.example.hs_kart.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hs_kart.MainActivity
import com.example.hs_kart.databinding.ActivityOtpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.button3.setOnClickListener {
            val otp = binding.userOTP.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please provide OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyUser(otp)
            }
        }
    }

    private fun verifyUser(otp: String) {
        val verificationId = intent.getStringExtra("verificationId")
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this, "Verification ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        saveUserToFirestore(user.phoneNumber!!)
                    }
                } else {
                    Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(phoneNumber: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(phoneNumber)

        userRef.get().addOnSuccessListener { document ->
            if (!document.exists()) {
                // Create new user entry in Firestore
                val userData = hashMapOf(
                    "phone" to phoneNumber,
                    "name" to "New User",
                    "email" to "",
                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )

                userRef.set(userData)
                    .addOnSuccessListener {
                        redirectToMain(phoneNumber)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // User already exists
                redirectToMain(phoneNumber)
            }
        }
    }

    private fun redirectToMain(phoneNumber: String) {
        val preferences = getSharedPreferences("users", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("number", phoneNumber)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
