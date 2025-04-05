package com.example.hs_kart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hs_kart.adapter.ProductAdapter
import com.example.hs_kart.databinding.ActivityAllProductsBinding
import com.example.hs_kart.model.AddProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AllProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllProductsBinding
    private lateinit var adapter: ProductAdapter
    private var productList = ArrayList<AddProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Products"

        // Initialize RecyclerView with GridLayout (2 columns)
        binding.productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(this, productList)
        binding.productsRecyclerView.adapter = adapter

        // Load products from Firebase
        loadProducts()
    }

    private fun loadProducts() {
        Firebase.firestore.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                productList.clear()
                for (document in querySnapshot) {
                    val product = document.toObject(AddProductModel::class.java)
                    productList.add(product)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
                exception.printStackTrace()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}