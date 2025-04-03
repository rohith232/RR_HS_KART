package com.example.hs_kart

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.hs_kart.activity.LoginActivity1
import com.example.hs_kart.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedItem = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        // In your Application class
//        FirebaseApp.initializeApp(this)
//        Firebase.appCheck.installAppCheckProviderFactory(
//            DebugAppCheckProviderFactory.getInstance()
//        )
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        auth.currentUser?.let { user ->
            // User is logged in, proceed with app
            initializeMainApp()
        } ?: run {
            // No user logged in, redirect to login
            redirectToLogin()
        }
    }

    private fun initializeMainApp() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()

        // Optional: Show welcome message with user email
        auth.currentUser?.email?.let { email ->
            Toast.makeText(this, "Welcome back, $email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity1::class.java))
        finish()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)

        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)

        binding.bottomBar.onItemSelected = {
            selectedItem = it
            when(it) {
                0 -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                }
                1 -> {
                    if (navController.currentDestination?.id != R.id.cartFragment) {
                        navController.navigate(R.id.cartFragment)
                    }
                }
                2 -> {
                    if (navController.currentDestination?.id != R.id.moreFragment) {
                        navController.navigate(R.id.moreFragment)
                    }
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = when(destination.id) {
                R.id.cartFragment -> "My Cart"
                R.id.moreFragment -> "My Dashboard"
                else -> "HS_KART"
            }
        }
    }

    fun logout() {
        auth.signOut()
        redirectToLogin()
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.homeFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}