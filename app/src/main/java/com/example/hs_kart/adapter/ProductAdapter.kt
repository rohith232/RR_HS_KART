package com.example.hs_kart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hs_kart.activity.ProductDetailActivity
import com.example.hs_kart.databinding.LayoutProductItemBinding
import com.example.hs_kart.model.AddProductModel

class ProductAdapter(val context: Context, val list: ArrayList<AddProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            LayoutProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = list[position]

        // ✅ Use itemView.context for Glide to avoid memory leaks
        Glide.with(holder.itemView.context)
            .load(data.productCoverImg)
            .into(holder.binding.imageView2)

        holder.binding.textView.text = data.productName
        holder.binding.textView3.text = data.productCategory
        holder.binding.textView4.text = data.productMrp

        // ✅ Prevent ViewHolder recycling issues
        holder.binding.button.text = ""
        holder.binding.button.text = data.productSp

        // ✅ Use itemView.context for Intent to prevent activity leaks
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            holder.itemView.context.startActivity(intent)
        }
    }
}
