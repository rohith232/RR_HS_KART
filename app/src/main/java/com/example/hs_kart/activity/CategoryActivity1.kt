package com.example.hs_kart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hs_kart.R
import com.example.hs_kart.adapter.CategoryAdapter
import com.example.hs_kart.databinding.ActivityCategory1Binding
import com.example.hs_kart.databinding.ActivityCategoryBinding
import com.example.hs_kart.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryActivity1 : AppCompatActivity() {
    private lateinit var binding: ActivityCategory1Binding
    private lateinit var adapter: CategoryAdapter
    private var categoryList = ArrayList<CategoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategory1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "All Categories"

        // Initialize RecyclerView with GridLayout (2 columns)
        binding.categoryRecyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = CategoryAdapter(this, categoryList)
        binding.categoryRecyclerView.adapter = adapter

        // Check if we came from a specific category click (optional)
        val categoryName = intent.getStringExtra("cat")
        if (!categoryName.isNullOrEmpty()) {
            supportActionBar?.title = categoryName
        }

        // Load categories from Firebase
        loadCategories()
    }

    private fun loadCategories() {
        Firebase.firestore.collection("category")
            .get()
            .addOnSuccessListener { querySnapshot ->
                categoryList.clear()
                for (document in querySnapshot) {
                    val category = document.toObject(CategoryModel::class.java)
                    categoryList.add(category)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}