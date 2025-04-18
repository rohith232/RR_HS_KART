package com.example.hs_kart.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.hs_kart.MainActivity
import com.example.hs_kart.R
import com.example.hs_kart.roomdb.AppDatabase
import com.example.hs_kart.roomdb.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject



class CheckoutActivity : AppCompatActivity(),PaymentResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)

        val checkout=Checkout()
        checkout.setKeyID("rzp_test_9a7WhsxXCVQmXP");

        val price=intent.getStringExtra("totalCost")
        try {
            val options = JSONObject()
            options.put("name", "HS_KART")
            options.put("description", "Best e-commerce app")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image", "http://example.com/image/rzp.jpg")
            options.put("theme.color", "#6200EE")
            options.put("currency", "INR")
            options.put("amount", (price!!.toInt()*100).toString())//pass amount in currency subunits

            options.put("prefill.email", "hs061819@gmail.com")
            options.put("prefill.contact", "8755816311")
            checkout.open(this, options)
        }
        catch (e:Exception){
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this,"Payment Success",Toast.LENGTH_SHORT).show()

        uploadData()
    }

    private fun uploadData() {
        val id=intent.getStringArrayListExtra("productIds")
        for(currentId in id!!){
            fetchData(currentId.toString())
        }
    }

    private fun fetchData(productId: String?) {

        val dao=AppDatabase.getInstance(this).productDao()

        Firebase.firestore.collection("products")
            .document(productId!!).get().addOnSuccessListener {

                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deleteProduct(ProductModel(productId))
                }

                saveData(it.getString("productName"),
                    it.getString("productSp"),
                    productId)
            }

    }

    private fun saveData(name: String?, price: String?, productId: String) {
        val preferences=this.getSharedPreferences("user", MODE_PRIVATE)
        val data= hashMapOf<String,Any>()
        data["name"]=name!!
        data["price"]=price!!
        data["productId"]=productId
        data["status"]="Ordered"
        data["userId"]=preferences.getString("number","")!!

        val firestore= Firebase.firestore.collection("allOrders")
        val key=firestore.document().id
        data["orderId"]=key

        firestore.document(key).set(data).addOnSuccessListener {
            Toast.makeText(this,"Order Placed",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
        }


    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this,"Payment Error",Toast.LENGTH_SHORT).show()
    }
}