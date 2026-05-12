package com.example.foyudemo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String = "",        // 👈 new field
    val serviceName: String,
    val packageName: String,
    val packagePrice: String,
    val date: String,
    val time: String,
    val address: String,
    val phone: String,
    val bookedAt: Long = System.currentTimeMillis()
)