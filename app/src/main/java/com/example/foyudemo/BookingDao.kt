package com.example.foyudemo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings WHERE userEmail = :email ORDER BY bookedAt DESC")
    fun getBookingsByUser(email: String): Flow<List<BookingEntity>>

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET date = :date, time = :time WHERE id = :id")
    suspend fun updateBookingDateTime(id: Int, date: String, time: String)
    @Query("DELETE FROM bookings WHERE userEmail = :email")
    suspend fun deleteAllBookingsByUser(email: String)
}