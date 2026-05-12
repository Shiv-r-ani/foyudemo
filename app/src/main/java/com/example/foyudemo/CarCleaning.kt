package com.example.foyudemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CarCleaning : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_cleaning)

        // Find each "Book Now" button and wire it up with the package details
        // The IDs below should match whatever you named your buttons in the XML.
        // If your buttons don't have IDs yet, add android:id to each Button in your XML.

        setupBookButton(R.id.btn1, "Car Cleaning", "Basic Wash",           "₹299")
        setupBookButton(R.id.btn2, "Car Cleaning", "Interior + Exterior",  "₹599")
        setupBookButton(R.id.btn3, "Car Cleaning", "Full Detailing",       "₹999")

        findViewById<android.widget.ImageView>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun setupBookButton(buttonId: Int, service: String, packageName: String, price: String) {
        findViewById<Button>(buttonId).setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java).apply {
                putExtra(BookingActivity.EXTRA_SERVICE_NAME,  service)
                putExtra(BookingActivity.EXTRA_PACKAGE_NAME,  packageName)
                putExtra(BookingActivity.EXTRA_PACKAGE_PRICE, price)
            }
            startActivity(intent)
        }
    }
}