package com.example.hs_kart.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.hs_kart.activity.AddressActivity
import com.example.hs_kart.adapter.CartAdapter
import com.example.hs_kart.databinding.FragmentCartBinding
import com.example.hs_kart.roomdb.AppDatabase
import com.example.hs_kart.roomdb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var list: ArrayList<String>
    private lateinit var quantities: HashMap<String, Int> // To track quantities by product ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        quantities = HashMap()

        context?.let {
            val preference = it.getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
            preference.edit().apply {
                putBoolean("isCart", false)
                apply()
            }
        }

        val dao = AppDatabase.getInstance(requireContext()).productDao()
        list = ArrayList()

        dao.getAllProducts().observe(viewLifecycleOwner) { products ->
            // Initialize quantities
            products.forEach { product ->
                quantities[product.productId] = product.quantity ?: 1
            }

            val adapter = CartAdapter(requireContext(), products).apply {
                onQuantityChanged = { productId, newQuantity ->
                    quantities[productId] = newQuantity
                    updateProductQuantityInDB(productId, newQuantity)
                    totalCost(products)
                }
            }
            binding.cartRecycler.adapter = adapter

            list.clear()
            products.forEach { list.add(it.productId) }
            totalCost(products)
        }

        return binding.root
    }

    private fun updateProductQuantityInDB(productId: String, quantity: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(requireContext()).productDao()
            val product = dao.getProductById(productId)
            product?.let {
                dao.updateProductQuantity(productId, quantity)
            }
        }
    }

    private fun totalCost(data: List<ProductModel>?) {
        if (data.isNullOrEmpty()) {
            binding.textView12.text = "(Total item: 0)"
            binding.textView13.text = "Total Cost: 0"
            return
        }

        val total = data.sumOf { product ->
            val cleanedPrice = product.productSp?.replace(Regex("[^0-9]"), "") ?: "0"
            val quantity = quantities[product.productId] ?: 1
            cleanedPrice.toInt() * quantity
        }

        val totalItems = data.sumOf { quantities[it.productId] ?: 1 }

        binding.textView12.text = "($totalItems)"
        binding.textView13.text = "Total Amount: ₹$total"

        binding.checkout.setOnClickListener {
            val intent = Intent(requireContext(), AddressActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putStringArrayList("productIds", list)
                    putString("totalCost", total.toString())
                    // Pass quantities to checkout
                    putSerializable("quantities", quantities)
                })
            }
            startActivity(intent)
        }
    }
}