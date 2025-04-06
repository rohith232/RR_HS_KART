package com.example.hs_kart.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private var filteredList = ArrayList<AddProductModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Products"

        // Initialize RecyclerView with GridLayout (1 column)
        binding.productsRecyclerView.layoutManager = GridLayoutManager(this, 1)
        adapter = ProductAdapter(this, filteredList)
        binding.productsRecyclerView.adapter = adapter

        // Set up search functionality
        setupSearchView()

        // Load products from Firebase
        loadProducts()
    }

    private fun setupSearchView() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        filteredList.clear()

        if (query.isEmpty()) {
            filteredList.addAll(productList)
        } else {
            val searchQuery = query.toLowerCase().trim()
            for (product in productList) {
                if (product.productName?.toLowerCase()?.contains(searchQuery) == true ||
                    product.productCategory?.toLowerCase()?.contains(searchQuery) == true ||
                    product.productDescription?.toLowerCase()?.contains(searchQuery) == true) {
                    filteredList.add(product)
                }
            }
        }
        adapter.notifyDataSetChanged()
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
                // Initialize filteredList with all products
                filteredList.addAll(productList)
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