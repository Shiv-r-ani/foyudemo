package com.example.foyudemo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class BookingsFragment : Fragment() {

    private lateinit var viewModel: BookingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_booking, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ← TEMPORARY TEST — remove after demo
        val testIntent = Intent(requireContext(), BookingReminderReceiver::class.java).apply {
            putExtra("service_name", "Plumbing")
        }
        requireContext().sendBroadcast(testIntent)


        viewModel = ViewModelProvider(requireActivity())[BookingViewModel::class.java]

        val rvBookings = view.findViewById<RecyclerView>(R.id.recyclerView)
        val tvEmpty    = view.findViewById<TextView>(R.id.textv)

        val adapter = BookingListAdapter(
            onShare      = { booking -> shareBooking(booking) },
            onCancel     = { booking -> confirmCancel(booking) },
            onReschedule = { booking -> showReschedulePicker(booking) }
        )

        rvBookings.layoutManager = LinearLayoutManager(requireContext())
        rvBookings.adapter = adapter

        viewModel.allBookings.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            tvEmpty.visibility    = if (list.isEmpty()) View.VISIBLE else View.GONE
            rvBookings.visibility = if (list.isEmpty()) View.GONE   else View.VISIBLE
        }
    }

    // ── Cancel: confirmation dialog → delete → toast ──────────────────────────
    private fun confirmCancel(booking: BookingEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel your ${booking.serviceName} booking on ${booking.date}?")
            .setPositiveButton("Cancel Booking") { _, _ ->
                viewModel.deleteBooking(booking)
                Toast.makeText(
                    requireContext(),
                    "Your booking has been cancelled ❌",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton("Keep Booking", null)
            .show()
    }

    // ── Reschedule: date picker → time picker → update DB → toast ─────────────
    private fun showReschedulePicker(booking: BookingEntity) {
        val cal = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, y, m, d ->
            val newDate = "$d ${monthName(m)} $y"

            TimePickerDialog(requireContext(), { _, hour, minute ->
                val amPm        = if (hour < 12) "AM" else "PM"
                val displayHour = if (hour % 12 == 0) 12 else hour % 12
                val newTime     = "$displayHour:${minute.toString().padStart(2, '0')} $amPm"

                viewModel.rescheduleBooking(booking.id, newDate, newTime)

                Toast.makeText(
                    requireContext(),
                    "Booking rescheduled to $newDate at $newTime ✅",
                    Toast.LENGTH_LONG
                ).show()

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            .also { it.datePicker.minDate = System.currentTimeMillis() }
            .show()
    }

    // ── Share booking details via any app ─────────────────────────────────────
    private fun shareBooking(booking: BookingEntity) {
        val text = """
            🏠 Foyu Booking Confirmed!
            Service  : ${booking.serviceName}
            Package  : ${booking.packageName} (${booking.packagePrice})
            Date     : ${booking.date} at ${booking.time}
            Address  : ${booking.address}
        """.trimIndent()

        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }, "Share booking via"
            )
        )
    }

    private fun monthName(month: Int): String {
        return listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )[month]
    }
}