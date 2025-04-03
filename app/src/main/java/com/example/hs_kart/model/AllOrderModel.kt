package com.example.hs_kart.model

data class AllOrderModel(
    var name:String?="",
    var orderId:String?="",
    val userId:String?="",
    var status:String?="",
    val productId:String?="",
    var price:String?="",
)
