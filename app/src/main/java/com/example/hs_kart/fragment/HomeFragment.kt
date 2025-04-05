package com.example.hs_kart.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.hs_kart.R
import com.example.hs_kart.activity.AllProductsActivity
import com.example.hs_kart.activity.CategoryActivity1
import com.example.hs_kart.activity.ProfileActivity
import com.example.hs_kart.adapter.CategoryAdapter
import com.example.hs_kart.adapter.ProductAdapter
import com.example.hs_kart.adapter.SliderAdapter
import com.example.hs_kart.databinding.FragmentHomeBinding
import com.example.hs_kart.model.AddProductModel
import com.example.hs_kart.model.CategoryModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    // View Binding with proper null safety
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Cannot access binding after onDestroyView or before onCreateView")

    // Slider Properties
    private val sliderHandler = Handler(Looper.getMainLooper())
    private var sliderRunnable: Runnable? = null
    private var currentPage = 0
    private val SLIDER_DELAY_MS = 3000L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        checkCartRedirect()
        loadData()
    }

    private fun initViews() {
        setupClickListeners()
        initializeSliderHandler()
    }

    private fun setupClickListeners() {
        binding.seeall.setOnClickListener { navigateToCategoryActivity() }
        binding.seeall1.setOnClickListener { navigateToAllProducts() }
        binding.menuIcon.setOnClickListener { navigateToProfile() }
    }

    private fun initializeSliderHandler() {
        sliderRunnable = Runnable {
            if (isAdded && _binding != null) {
                val itemCount = binding.sliderViewPager.adapter?.itemCount ?: 0
                if (itemCount > 0) {
                    currentPage = if (currentPage == itemCount - 1) 0 else currentPage + 1
                    binding.sliderViewPager.setCurrentItem(currentPage, true)
                    sliderHandler.postDelayed(sliderRunnable!!, SLIDER_DELAY_MS)
                }
            }
        }
    }

    private fun checkCartRedirect() {
        if (!isAdded) return

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        if (preference.getBoolean("isCart", false)) {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }
    }

    private fun loadData() {
        getCategory()
        getSliderImage()
        getProducts()
    }

    private fun startAutoSlider(totalItems: Int) {
        if (!isAdded) return

        sliderHandler.removeCallbacks(sliderRunnable ?: return)
        if (totalItems > 0) {
            sliderHandler.postDelayed(sliderRunnable ?: return, SLIDER_DELAY_MS)
        }

        binding.sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (isAdded) {
                    currentPage = position
                    updateSliderDots(position, totalItems)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable ?: return)
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.removeCallbacks(sliderRunnable ?: return)
                    sliderHandler.postDelayed(sliderRunnable ?: return, SLIDER_DELAY_MS)
                }
            }
        })
    }

    private fun updateSliderDots(position: Int, totalItems: Int) {
        if (!isAdded) return

        val tabLayout = binding.sliderViewPager.getChildAt(1) as? TabLayout
        tabLayout?.let {
            for (i in 0 until it.tabCount) {
                it.getTabAt(i)?.view?.background = ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) R.drawable.dot_indicator_selected else R.drawable.dot_indicator
                )
            }
        }
    }

    private fun navigateToCategoryActivity() {
        if (!isAdded) return

        val intent = Intent(requireContext(), CategoryActivity1::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        ).toBundle()
        startActivity(intent, options)
    }

    private fun navigateToAllProducts() {
        if (!isAdded) return

        val intent = Intent(requireContext(), AllProductsActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        ).toBundle()
        startActivity(intent, options)
    }

    private fun navigateToProfile() {
        if (!isAdded) return

        val intent = Intent(requireContext(), ProfileActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.slide_in_right,
            R.anim.slide_out_left
        ).toBundle()
        startActivity(intent, options)
    }

    private fun getSliderImage() {
        if (!isAdded) return

        Firebase.firestore.collection("slider").document("item")
            .get()
            .addOnSuccessListener { document ->
                if (!isAdded) return@addOnSuccessListener

                val images = (document.get("img") as? List<String>)?.filterNotNull()
                if (!images.isNullOrEmpty()) {
                    binding.sliderViewPager.adapter = SliderAdapter(requireContext(), ArrayList(images))
                    setupSliderDots(images.size)
                    startAutoSlider(images.size)
                }
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error loading slider images", e)
            }
    }

    private fun setupSliderDots(count: Int) {
        if (!isAdded) return

        val tabLayout = binding.sliderViewPager.getChildAt(1) as? TabLayout
        tabLayout?.let {
            for (i in 0 until count) {
                it.addTab(it.newTab())
            }
            updateSliderDots(0, count)
        }
    }

    private fun getProducts() {
        if (!isAdded) return

        Firebase.firestore.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!isAdded) return@addOnSuccessListener

                val products = querySnapshot.documents.mapNotNull {
                    it.toObject(AddProductModel::class.java)
                }
                binding.productRecycler.adapter = ProductAdapter(requireContext(), products)
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error loading products", e)
            }
    }

    private fun getCategory() {
        if (!isAdded) return

        Firebase.firestore.collection("category")
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!isAdded) return@addOnSuccessListener

                val categories = querySnapshot.documents.mapNotNull {
                    it.toObject(CategoryModel::class.java)
                }
                binding.categoryRecycler.adapter = CategoryAdapter(requireContext(), categories)
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error loading categories", e)
            }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable ?: return)
    }

    override fun onResume() {
        super.onResume()
        if (isAdded && _binding != null) {
            binding.sliderViewPager.adapter?.itemCount?.takeIf { it > 0 }?.let {
                startAutoSlider(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sliderHandler.removeCallbacksAndMessages(null)
        sliderRunnable = null
        _binding = null
    }
}