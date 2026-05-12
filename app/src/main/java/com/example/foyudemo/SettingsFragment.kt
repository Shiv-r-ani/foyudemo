package com.example.foyudemo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session  = SessionManager(requireContext())
        val settings = SettingsManager(requireContext())

        // Greeting
        view.findViewById<TextView>(R.id.tvGreeting).text =
            "Hi, ${session.getUserName() ?: "there"} 👋"

        // Notifications
        val switchNotifications = view.findViewById<Switch>(R.id.switchNotifications)
        switchNotifications.isChecked = settings.isNotificationsEnabled()
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            settings.setNotificationsEnabled(isChecked)
            if (isChecked) {
                NotificationScheduler.scheduleDailyReminder(requireContext())
                Toast.makeText(requireContext(), "Notifications enabled ✅", Toast.LENGTH_SHORT).show()
            } else {
                NotificationScheduler.cancelDailyReminder(requireContext())
                Toast.makeText(requireContext(), "Notifications disabled ❌", Toast.LENGTH_SHORT).show()
            }
        }

        // Logout
        view.findViewById<LinearLayout>(R.id.layoutLogout).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    session.clearSession()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // Delete Account
        view.findViewById<LinearLayout>(R.id.layoutDeleteAccount).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure? This will permanently delete your account and all your bookings. This cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    deleteAccount(session)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun deleteAccount(session: SessionManager) {
        val email = session.getUserEmail() ?: ""

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())

            // Delete all bookings belonging to this user
            db.bookingDao().deleteAllBookingsByUser(email)

            // Delete the user account from users table
            db.userDao().deleteUserByEmail(email)

            // Clear session
            session.clearSession()

            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Account deleted successfully 🗑️",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate back to login
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}