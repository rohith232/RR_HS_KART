package com.example.hs_kart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hs_kart.adapter.FAQAdapter
import com.example.hs_kart.databinding.ActivityFaqactivityBinding
import com.example.hs_kart.model.FAQItem

class FAQActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaqactivityBinding
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar Setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "FAQs"

        // Initialize RecyclerView
        setupFAQRecyclerView()
    }

    private fun setupFAQRecyclerView() {
        val faqList = listOf(
            FAQItem(
                "How do I place an order?",
                "Select products > Add to cart > Proceed to checkout > Enter shipping details > Make payment"
            ),
            FAQItem(
                "What payment methods are accepted?",
                "We accept Credit/Debit cards, UPI, Net Banking, and Cash on Delivery"
            ),
            FAQItem(
                "How can I track my order?",
                "Go to 'My Orders' section where you'll find real-time tracking information"
            ),
            FAQItem(
                "What is your return policy?",
                "Items can be returned within 7 days of delivery if unused with original packaging"
            ),
            FAQItem(
                "How do I contact customer support?",
                "Tap 'Help Center' in the app menu or email us at support@QuickCart.com"
            )
        )

        faqAdapter = FAQAdapter(faqList)
        binding.faqRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@FAQActivity)
            adapter = faqAdapter
            setHasFixedSize(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}