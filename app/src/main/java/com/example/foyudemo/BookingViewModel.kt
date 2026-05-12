package com.example.foyudemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).bookingDao()
    private val session = SessionManager(application)

    val allBookings = dao.getBookingsByUser(
        session.getUserEmail() ?: ""
    ).asLiveData()

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            dao.deleteBooking(booking)
        }
    }

    fun rescheduleBooking(id: Int, date: String, time: String) {
        viewModelScope.launch {
            dao.updateBookingDateTime(id, date, time)
        }
    }
}