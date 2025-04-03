package com.example.hs_kart.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.hs_kart.MainActivity
import com.example.hs_kart.R
import com.example.hs_kart.databinding.ActivityProfileBinding
import com.example.hs_kart.fragment.CartFragment
import com.example.hs_kart.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { uri ->
                imageUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.profileImage)
                uploadImage()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = Firebase.auth
        database = Firebase.database.reference
        storageReference = FirebaseStorage.getInstance().reference

        setupClickListeners()
        loadUserData()
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("SHOW_CART_FRAGMENT", true)
        }
            startActivity(intent) }
        binding.cartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("SHOW_CART_FRAGMENT", true)
            }
            startActivity(intent)        }
        binding.editImageButton.setOnClickListener { openImagePicker() }
        binding.editProfileButton.setOnClickListener { enableEditing(true) }
        binding.saveProfileButton.setOnClickListener { saveProfileChanges() }
        binding.logoutButton.setOnClickListener { logoutUser() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicker.launch(intent)
    }

    private fun uploadImage() {
        val userId = auth.currentUser?.uid ?: return
        imageUri?.let { uri ->
            val imageRef = storageReference.child("profile_images/$userId.jpg")
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfileImage(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateProfileImage(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child("Users").child(userId).child("profileImage")
            .setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return

        database.child("Users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    binding.nameEditText.setText(it.name)
                    binding.emailEditText.setText(it.email)
                    binding.phoneEditText.setText(it.phone)

                    if (!it.profileImage.isNullOrEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(it.profileImage)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(binding.profileImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun enableEditing(enable: Boolean) {
        binding.nameEditText.isEnabled = enable
        binding.phoneEditText.isEnabled = enable
        binding.editProfileButton.visibility = if (enable) View.GONE else View.VISIBLE
        binding.saveProfileButton.visibility = if (enable) View.VISIBLE else View.GONE
    }

    private fun saveProfileChanges() {
        val userId = auth.currentUser?.uid ?: return
        val name = binding.nameEditText.text.toString().trim()
        val phone = binding.phoneEditText.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone" to phone
        )

        database.child("Users").child(userId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                enableEditing(false)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logoutUser() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity1::class.java))
        finishAffinity()
    }
}