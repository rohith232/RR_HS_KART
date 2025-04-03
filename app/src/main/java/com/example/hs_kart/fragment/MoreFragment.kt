package com.example.hs_kart.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hs_kart.R
import com.example.hs_kart.activity.LoginActivity1
import com.example.hs_kart.activity.ProfileActivity
import com.example.hs_kart.databinding.FragmentMoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        loadUserName()
    }

    private fun loadUserName() {
        val userId = auth.currentUser?.uid ?: run {
            binding.tvUserEmail.text = getString(R.string.guest_user)
            return
        }

        database.child("Users").child(userId).child("name")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userName = snapshot.getValue(String::class.java)
                    userName?.let {
                        binding.tvUserEmail.text = getString(R.string.welcome_user, it)
                    } ?: run {
                        // Fallback to email if name doesn't exist
                        auth.currentUser?.email?.let { email ->
                            binding.tvUserEmail.text = getString(R.string.welcome_user, email)
                        } ?: run {
                            binding.tvUserEmail.text = getString(R.string.guest_user)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    auth.currentUser?.email?.let { email ->
                        binding.tvUserEmail.text = getString(R.string.welcome_user, email)
                    } ?: run {
                        binding.tvUserEmail.text = getString(R.string.guest_user)
                    }
                }
            })
    }

    private fun setupViews() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity1::class.java))
            requireActivity().finish()
            Toast.makeText(requireContext(), R.string.logged_out_success, Toast.LENGTH_SHORT).show()
        }

        binding.btnOrderDetails.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_orderDetailsFragment)
        }

        binding.profile.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
            requireActivity().finish()
        }
    }
}