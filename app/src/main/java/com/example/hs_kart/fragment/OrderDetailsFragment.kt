package com.example.hs_kart.fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hs_kart.R
import com.example.hs_kart.adapter.AllOrderAdapter
import com.example.hs_kart.databinding.FragmentOrderDetailsBinding
import com.example.hs_kart.model.AllOrderModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private lateinit var list: ArrayList<AllOrderModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.ivBackArrow.setOnClickListener {
            // Handle back navigation
            findNavController().popBackStack()
        }

        binding.ivCart.setOnClickListener {
            // Navigate to cart
            findNavController().navigate(R.id.action_to_cart)
        }
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView (your existing code)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        list = ArrayList()
        loadOrders()

        // Handle back press
        setupBackPressHandler()
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigate back to previous fragment
                    findNavController().popBackStack()
                }
            }
        )
    }

    // YOUR EXISTING loadOrders() METHOD REMAINS UNCHANGED
    private fun loadOrders() {
        binding.progressBar.visibility = View.VISIBLE

        val preferences = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        val userId = preferences.getString("number", "") ?: ""

        Firebase.firestore.collection("allOrders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                list.clear()

                for (doc in querySnapshot) {
                    val data = doc.toObject(AllOrderModel::class.java)
                    list.add(data)
                }

                binding.recyclerView.adapter = AllOrderAdapter(list, requireContext())
                binding.progressBar.visibility = View.GONE

                if (list.isEmpty()) {
                    binding.tvNoOrders.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.tvNoOrders.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}