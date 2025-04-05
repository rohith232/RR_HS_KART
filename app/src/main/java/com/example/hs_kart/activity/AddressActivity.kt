package com.example.hs_kart.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hs_kart.MainActivity
import com.example.hs_kart.R
import com.example.hs_kart.databinding.ActivityAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var totalCost: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddressBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        totalCost = intent.getStringExtra("totalCost") ?: "0"

        if (auth.currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        loadUserInfo()
    }

    private fun initializeViews() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra("openCart", true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }

        binding.proceed.setOnClickListener {
            validateData(
                binding.userNumber.text.toString(),
                binding.userName.text.toString(),
                binding.userVillage.text.toString(),
                binding.userPincode.text.toString(),
                binding.userCity.text.toString(),
                binding.userState.text.toString()
            )
        }
    }

    private fun validateData(
        number: String,
        name: String,
        village: String,
        pinCode: String,
        city: String,
        state: String
    ) {
        if (number.isEmpty() || name.isEmpty() || village.isEmpty() ||
            pinCode.isEmpty() || city.isEmpty() || state.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
        } else {
            storeData(number, name, pinCode, city, state, village)
        }
    }

    private fun storeData(
        number: String,
        name: String,
        pinCode: String,
        city: String,
        state: String,
        village: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = hashMapOf(
            "userPhoneNumber" to number,
            "userName" to name,
            "pinCode" to pinCode,
            "city" to city,
            "state" to state,
            "village" to village,
            "userId" to currentUser.uid
        )

        Firebase.firestore.collection("users")
            .document(currentUser.uid) // Using Firebase UID as document ID
            .set(userData)
            .addOnSuccessListener {
                val productIds = intent.getStringArrayListExtra("productIds")
                startActivity(Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("productIds", productIds)
                    putExtra("totalCost", totalCost)
                })
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to save address: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun loadUserInfo() {
        val currentUser = auth.currentUser ?: run {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Firebase.firestore.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.userName.setText(document.getString("userName") ?: "")
                    binding.userNumber.setText(document.getString("userPhoneNumber") ?: "")
                    binding.userVillage.setText(document.getString("village") ?: "")
                    binding.userState.setText(document.getString("state") ?: "")
                    binding.userCity.setText(document.getString("city") ?: "")
                    binding.userPincode.setText(document.getString("pinCode") ?: "")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load address: ${e.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}