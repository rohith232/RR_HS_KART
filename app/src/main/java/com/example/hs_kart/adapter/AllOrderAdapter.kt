package com.example.hs_kart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.hs_kart.R
import com.example.hs_kart.databinding.AllOrderItemLayoutBinding
import com.example.hs_kart.model.AllOrderModel
import com.google.firebase.firestore.FirebaseFirestore


class AllOrderAdapter(val list:ArrayList<AllOrderModel>, val context: Context):RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>() {

    inner class AllOrderViewHolder(val binding : AllOrderItemLayoutBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllOrderViewHolder {
        return AllOrderViewHolder(
            AllOrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AllOrderViewHolder, position: Int) {
        holder.binding.productTitle.text=list[position].name
        holder.binding.productPrice.text=list[position].price


        when(list[position].status){
            "Ordered" ->{
                holder.binding.productStatus.apply {
                    text = "Ordered"
                    setTextColor(context.getColor(R.color.status_text_light))
                    background = context.getDrawable(R.drawable.status_bg_ordered)
                }
               // holder.binding.productStatus.text="Ordered"

            }
            "Dispatched" ->{
                holder.binding.productStatus.apply {
                    text = "Dispatched"
                    setTextColor(context.getColor(R.color.status_text_light))
                    background = context.getDrawable(R.drawable.status_bg_dispatched)
                }
                //holder.binding.productStatus.text="Dispatched"
            }
            "Delivered" ->{
                holder.binding.productStatus.apply {
                    text = "Delivered"
                    setTextColor(context.getColor(R.color.status_text_light))
                    background = context.getDrawable(R.drawable.status_bg_delivered)
                }
               // holder.binding.productStatus.text="Delivered"
            }
            "Canceled" ->{
                holder.binding.productStatus.apply {
                    text = "Canceled"
                    setTextColor(context.getColor(R.color.status_text_light))
                    background = context.getDrawable(R.drawable.status_bg_canceled)
                }
                //holder.binding.productStatus.text="Canceled"
            }

        }
    }
}