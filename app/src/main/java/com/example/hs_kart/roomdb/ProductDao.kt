package com.example.hs_kart.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert

    suspend fun insertProduct(product:ProductModel)
    @Delete
    suspend fun deleteProduct(product:ProductModel)
    @Query("SELECT * FROM products")
    fun getAllProducts():LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId=:id")
    fun isExit(id:String):ProductModel

    @Query("UPDATE products SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateProductQuantity(productId: String, quantity: Int)

    @Query("SELECT * FROM products WHERE productId = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductModel?
}