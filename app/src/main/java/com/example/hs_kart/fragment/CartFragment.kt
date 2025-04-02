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

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var list:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // Avoid null context issues
        context?.let {
            val preference = it.getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
            preference.edit().apply {
                putBoolean("isCart", false)
                apply()
            }
        }

        val dao = AppDatabase.getInstance(requireContext()).productDao()
        list=ArrayList()
        dao.getAllProducts().observe(viewLifecycleOwner) {
            binding.cartRecycler.adapter = CartAdapter(requireContext(), it)

            list.clear()
            for(data in it){
                list.add(data.productId)
            }
            totalCost(it)
        }

        return binding.root
    }
    private fun totalCost(data: List<ProductModel>?) {
        if (data.isNullOrEmpty()) {
            binding.textView12.text = "Total item in cart is: 0"
            binding.textView13.text = "Total Cost: 0"
            return
        }

        val total = data.sumOf {
            // Clean the productSp string to remove any non-numeric characters
            val cleanedPrice = it.productSp?.replace(Regex("[^0-9]"), "") ?: "0"
            cleanedPrice.toInt()  // Now parse it safely to an integer
        }

        binding.textView12.text = "Total item in cart is: ${data.size}"
        binding.textView13.text = "Total Cost: ₹$total"  // You can format it here to include currency symbol

        binding.checkout.setOnClickListener {
            val intent = Intent(requireContext(), AddressActivity::class.java)
            val b = Bundle()
            b.putStringArrayList("productIds", list)
            b.putString("totalCost", total.toString())
            intent.putExtras(b)
            startActivity(intent)
        }
    }


//    private fun totalCost(data: List<ProductModel>?) {
//        if (data.isNullOrEmpty()) {
//            binding.textView12.text = "Total item in cart is: 0"
//            binding.textView13.text = "Total Cost: 0"
//            return
//        }
//
//        val total = data.sumOf { it.productSp?.toInt() ?: 0 }
//
//        binding.textView12.text = "Total item in cart is: ${data.size}"
//        binding.textView13.text = "Total Cost: $total"
//
//        binding.checkout.setOnClickListener {
//            val intent = Intent(requireContext(), AddressActivity::class.java)
//            val b=Bundle()
//            b.putStringArrayList("productIds",list)
//            b.putString("totalCost",total.toString())
//            intent.putExtras(b)
//            startActivity(intent)
//        }
//    }
}
