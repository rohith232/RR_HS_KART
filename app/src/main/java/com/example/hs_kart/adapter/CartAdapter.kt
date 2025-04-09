package com.example.hs_kart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hs_kart.activity.ProductDetailActivity
import com.example.hs_kart.databinding.LayoutCartItemBinding
import com.example.hs_kart.roomdb.AppDatabase
import com.example.hs_kart.roomdb.ProductModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CartAdapter(val context: Context, private val list: List<ProductModel>) :
    RecyclerView.Adapter<CartAdapter.CardViewHolder>() {

    var onQuantityChanged: ((productId: String, newQuantity: Int) -> Unit)? = null
    private val quantityAdapter by lazy {
        ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            (1..10).map { it.toString() }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    inner class CardViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var userSelected = false
        private var currentProduct: ProductModel? = null

        fun bind(product: ProductModel) {
            currentProduct = product

            // Load image efficiently
            Glide.with(context)
                .load(product.productImage)
                .centerCrop()
                .into(binding.imageView4)

            binding.textView11.text = product.productName
            binding.textView12.text = product.productSp

            // Reuse the shared adapter
            binding.quantitySpinner.adapter = quantityAdapter

            // Set current quantity without triggering callback
            userSelected = false
            binding.quantitySpinner.setSelection((product.quantity ?: 1) - 1, false)

            // Set up item click listeners
            binding.root.setOnClickListener {
                context.startActivity(
                    Intent(context, ProductDetailActivity::class.java).apply {
                        putExtra("id", product.productId)
                    }
                )
            }

            binding.imageView5.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(context).productDao().deleteProduct(product)
                }
            }
        }

        init {
            binding.quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    if (userSelected) {
                        currentProduct?.let { product ->
                            val newQuantity = position + 1
                            onQuantityChanged?.invoke(product.productId, newQuantity)
                        }
                    }
                    userSelected = true
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutCartItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}