package com.example.foyudemo



import android.content.Context

class SettingsManager(context: Context) {

    private val prefs = context.getSharedPreferences("foyu_settings", Context.MODE_PRIVATE)

    companion object {
        const val KEY_NOTIFICATIONS = "notifications_enabled"

    }

    fun setNotificationsEnabled(enabled: Boolean) =
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()

    fun isNotificationsEnabled(): Boolean =
        prefs.getBoolean(KEY_NOTIFICATIONS, true)


}