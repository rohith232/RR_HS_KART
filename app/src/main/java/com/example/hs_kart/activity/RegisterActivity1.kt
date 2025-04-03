// app/src/main/java/com/example/hs_kart/activity/RegisterActivity1.kt
package com.example.hs_kart.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hs_kart.R
import com.example.hs_kart.databinding.ActivityRegister1Binding
import com.example.hs_kart.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityRegister1Binding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegister1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val name = binding.nameEditText.text.toString().trim()
            val phone = binding.phoneEditText.text.toString().trim()

            if (validateInputs(email, password, name, phone)) {
                registerUser(email, password, name, phone)
            }
        }
        binding.signin.setOnClickListener {
            // In your current activity
            val intent=Intent(this,LoginActivity1::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(email: String, password: String, name: String, phone: String): Boolean {
        var valid = true

        if (email.isEmpty()) {
            binding.emailEditText.error = "Email required"
            binding.emailEditText.requestFocus()
            valid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Invalid email"
            binding.emailEditText.requestFocus()
            valid = false
        }

        if (password.isEmpty()) {
            binding.passwordEditText.error = "Password required"
            binding.passwordEditText.requestFocus()
            valid = false
        } else if (password.length < 6) {
            binding.passwordEditText.error = "Password too short"
            binding.passwordEditText.requestFocus()
            valid = false
        }

        if (name.isEmpty()) {
            binding.nameEditText.error = "Name required"
            binding.nameEditText.requestFocus()
            valid = false
        }

        if (phone.isEmpty()) {
            binding.phoneEditText.error = "Phone required"
            binding.phoneEditText.requestFocus()
            valid = false
        }

        return valid
    }

    private fun registerUser(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration successful, now save additional user data
                    val userId = auth.currentUser?.uid ?: ""

                    val user = User(
                        userId = userId,
                        name = name,
                        email = email,
                        phone = phone
                    )

                    // Save to Realtime Database
                    databaseReference.child(userId).setValue(user)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity1::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to save user data: ${dbTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}