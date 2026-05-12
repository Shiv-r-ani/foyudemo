package com.example.foyudemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ElectricianActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electrician)

        setupBookButton(R.id.btn11, "Electricity", "Single Item Assembly", "₹299")
        setupBookButton(R.id.btn22, "Electricity", "Room Setup",           "₹799")
        setupBookButton(R.id.btn33, "Electricity", "Full Home Setup",      "₹1,999")
        setupBookButton(R.id.btn44, "Electricity", "Single Item Assembly", "₹299")
        setupBookButton(R.id.btn55, "Electricity", "Single Item Assembly", "₹299")
        setupBookButton(R.id.btn66, "Electricity", "Single Item Assembly", "₹299")

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