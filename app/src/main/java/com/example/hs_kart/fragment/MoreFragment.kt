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
import com.example.hs_kart.databinding.FragmentMoreBinding
import com.google.firebase.auth.FirebaseAuth

class MoreFragment : Fragment() {
    private lateinit var binding: FragmentMoreBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        setupViews()
        return binding.root
    }

    private fun setupViews() {
        auth.currentUser?.email?.let { email ->
            binding.tvUserEmail.text = getString(R.string.welcome_user, email)
        } ?: run {
            binding.tvUserEmail.text = getString(R.string.guest_user)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity1::class.java))
            requireActivity().finish()
            Toast.makeText(requireContext(), R.string.logged_out_success, Toast.LENGTH_SHORT).show()
        }

        binding.btnOrderDetails.setOnClickListener {
            findNavController().navigate(R.id.action_moreFragment_to_orderDetailsFragment)
        }
    }
}