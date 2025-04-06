package com.example.hs_kart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hs_kart.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About Us"

        // Developer info
        binding.developer1Name.text = "Rohith Reddy"
        binding.developer2Name.text = "Harsh Sharma"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}