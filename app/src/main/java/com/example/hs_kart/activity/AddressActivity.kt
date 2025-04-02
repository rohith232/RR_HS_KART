package com.example.hs_kart.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hs_kart.R
import com.example.hs_kart.databinding.ActivityAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class AddressActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddressBinding
    private lateinit var preferences:SharedPreferences

    private lateinit var totalCost:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityAddressBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        preferences=this.getSharedPreferences("users", MODE_PRIVATE)

        totalCost=intent.getStringExtra("totalCost")!!


        loadUserInfo()

        binding.proceed.setOnClickListener{
            validateData(
                binding.userNumber.text.toString(),
                binding.userName.text.toString(),
                binding.userVillage.text.toString(),
                binding.userPincode.text.toString(),
                binding.userState.text.toString(),
                binding.userCity.text.toString()
            )
        }


    }

    private fun validateData(number: String,name: String,pinCode:String,city:String,state:String,village:String) {
        if(number.isEmpty() || state.isEmpty() || name.isEmpty() || city.isEmpty() || pinCode.isEmpty()){
            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_LONG).show()
        }else{
            storeData(pinCode,city,state,village)
        }
    }

    private fun storeData(pinCode: String, city: String, state: String, village: String) {
        val map = hashMapOf<String,Any>()
        map["village"]=village
        map["state"]=state
        map["city"]=city
        map["pinCode"]=pinCode

        Firebase.firestore.collection("users")
            .document(preferences.getString("number","")!!)
            .update(map).addOnSuccessListener {
                val b=Bundle()
                b.putStringArrayList("productIds",getIntent().getStringArrayListExtra("productIds"))
                b.putString("totalCost",totalCost.toString())
                intent.putExtras(b)
                val intent=Intent(this,CheckoutActivity::class.java)
                intent.putExtras(b)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
            }

    }


    private fun loadUserInfo(){
        val preferences = this.getSharedPreferences("users", MODE_PRIVATE)
        Firebase.firestore.collection("users")
            .document(preferences.getString("number","")!!)
            .get().addOnSuccessListener {
                binding.userName.setText(it.getString("userName"))
                binding.userNumber.setText(it.getString("userPhoneNumber"))
                binding.userVillage.setText(it.getString("village"))
                binding.userState.setText(it.getString("state"))
                binding.userCity.setText(it.getString("city"))
                binding.userPincode.setText(it.getString("pinCode"))
            }.addOnFailureListener {
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show()
            }
    }
}