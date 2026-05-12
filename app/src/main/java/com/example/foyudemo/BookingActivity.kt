package com.example.foyudemo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*

class BookingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SERVICE_NAME  = "service_name"
        const val EXTRA_PACKAGE_NAME  = "package_name"
        const val EXTRA_PACKAGE_PRICE = "package_price"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val serviceName  = intent.getStringExtra(EXTRA_SERVICE_NAME)  ?: "Service"
        val packageName  = intent.getStringExtra(EXTRA_PACKAGE_NAME)  ?: "Package"
        val packagePrice = intent.getStringExtra(EXTRA_PACKAGE_PRICE) ?: ""

        val tvServiceSummary = findViewById<TextView>(R.id.tvServiceSummary)
        val etDate           = findViewById<EditText>(R.id.etDate)
        val etTime           = findViewById<EditText>(R.id.etTime)
        val etAddress        = findViewById<EditText>(R.id.etAddress)
        val etPhone          = findViewById<EditText>(R.id.etPhone)
        val btnConfirm       = findViewById<Button>(R.id.btnConfirmBooking)
        val btnBack          = findViewById<ImageView>(R.id.btnBack)

        tvServiceSummary.text = "$serviceName — $packageName ($packagePrice)"

        btnBack.setOnClickListener { finish() }

        etDate.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val cal = Calendar.getInstance()
                DatePickerDialog(this, { _, y, m, d ->
                    etDate.setText("$d ${monthName(m)} $y")
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                    .also { it.datePicker.minDate = System.currentTimeMillis() }
                    .show()
            }
            true
        }

        etTime.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_UP) {
                val cal = Calendar.getInstance()
                TimePickerDialog(this, { _, hour, minute ->
                    val amPm = if (hour < 12) "AM" else "PM"
                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                    etTime.setText("$displayHour:${minute.toString().padStart(2, '0')} $amPm")
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
            }
            true
        }

        btnConfirm.setOnClickListener {
            val date    = etDate.text.toString().trim()
            val time    = etTime.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val phone   = etPhone.text.toString().trim()

            when {
                date.isEmpty()    -> { etDate.error = "Please pick a date"; return@setOnClickListener }
                time.isEmpty()    -> { etTime.error = "Please pick a time"; return@setOnClickListener }
                address.isEmpty() -> { etAddress.error = "Please enter your address"; return@setOnClickListener }
                phone.length < 10 -> { etPhone.error = "Enter a valid phone number"; return@setOnClickListener }
            }

            // 👇 get current user email from session
            val session = SessionManager(this)

            val booking = BookingEntity(
                userEmail    = session.getUserEmail() ?: "",   // 👈 links booking to user
                serviceName  = serviceName,
                packageName  = packageName,
                packagePrice = packagePrice,
                date         = date,
                time         = time,
                address      = address,
                phone        = phone
            )

            lifecycleScope.launch {
                AppDatabase.getInstance(this@BookingActivity)
                    .bookingDao()
                    .insertBooking(booking)
                // ← ADD THIS — converts picked date/time to milliseconds and schedules reminder
                val bookingTimeMillis = convertToMillis(date, time)
                NotificationScheduler.scheduleBookingReminder(
                    this@BookingActivity,
                    serviceName,
                    bookingTimeMillis
                )


                runOnUiThread {
                    Toast.makeText(this@BookingActivity, "Booking confirmed! 🎉", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
    private fun convertToMillis(date: String, time: String): Long {
        return try {
            // date format: "11 May 2026", time format: "10:45 AM"
            val sdf = java.text.SimpleDateFormat("d MMM yyyy hh:mm a", Locale.ENGLISH)
            sdf.parse("$date $time")?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun monthName(month: Int): String {
        val months = listOf("Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec")
        return months[month]
    }
}