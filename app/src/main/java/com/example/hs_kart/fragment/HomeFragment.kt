package com.example.hs_kart.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.hs_kart.R
import com.example.hs_kart.adapter.CategoryAdapter
import com.example.hs_kart.adapter.ProductAdapter
import com.example.hs_kart.adapter.SliderAdapter
import com.example.hs_kart.databinding.FragmentHomeBinding
import com.example.hs_kart.model.AddProductModel
import com.example.hs_kart.model.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        if(preference.getBoolean("isCart",false))
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        getCategory()
        getSliderImage()
        getProducts()
        return binding.root
    }
    private fun getSliderImage() {
        val sliderImages = ArrayList<String>()

        Firebase.firestore.collection("slider")  // Collection: "slider"
            .document("item")  // Document: "item"
            .get()  // Get the document
            .addOnSuccessListener { document ->
                // Check if the "img" field is an array of strings
                val imageUrls = document.get("img") as? List<String>  // Use get() instead of getString() since it's an array
                if (imageUrls != null) {
                    sliderImages.addAll(imageUrls)  // Add all images from the list to sliderImages
                }
                // Set the adapter for your ViewPager
                binding.sliderViewPager.adapter = SliderAdapter(requireContext(), sliderImages)
            }
            .addOnFailureListener { exception ->
                // Handle failure, if needed
                Log.e("SliderImage", "Error getting document: ", exception)
            }
    }

//    private fun getSliderImage() {
//        val sliderImages = ArrayList<String>()
//
//        Firebase.firestore.collection("slider")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val imageUrl = document.getString("img")
//                    if (imageUrl != null) {
//                        sliderImages.add(imageUrl)
//                    }
//                }
//                binding.sliderViewPager.adapter = SliderAdapter(requireContext(), sliderImages)
//            }
//
//
////        Firebase.firestore.collection("slider").document("item")
////            .get().addOnSuccessListener {
////                Glide.with(requireContext()).load(it.get("img")).into(binding.sliderImage)
////
////            }
//    }

    private fun getProducts() {
        val list = ArrayList<AddProductModel>()
        Firebase.firestore.collection("products")
            .get().addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(AddProductModel::class.java)
                    list.add(data!!)
                }
                binding.productRecycler.adapter = ProductAdapter(requireContext(),list)
            }
    }

    private fun getCategory() {
        val list = ArrayList<CategoryModel>()
        Firebase.firestore.collection("category")
            .get().addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(CategoryModel::class.java)
                    list.add(data!!)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(),list)
            }
    }

}