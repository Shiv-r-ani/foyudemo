package com.example.foyudemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class PlumbingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plumbing)

        setupBookButton(R.id.bttn1, "Plumbing", "Basic Repair",           "₹349")
        setupBookButton(R.id.bttn2, "Plumbing", "Standard Plumbing",      "₹699")
        setupBookButton(R.id.bttn3, "Plumbing", "Full Plumbing Overhaul", "₹1,499")

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupBookButton(buttonId: Int, service: String, pkg: String, price: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java).apply {
                putExtra(BookingActivity.EXTRA_SERVICE_NAME,  service)
                putExtra(BookingActivity.EXTRA_PACKAGE_NAME,  pkg)
                putExtra(BookingActivity.EXTRA_PACKAGE_PRICE, price)
            })
        }
    }
}