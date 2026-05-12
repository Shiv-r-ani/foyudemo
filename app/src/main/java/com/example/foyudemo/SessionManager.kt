package com.example.foyudemo


import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("foyu_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_EMAIL   = "user_email"
        const val KEY_USER_NAME    = "user_name"
        const val KEY_USER_PHONE   = "user_phone"
    }

    fun saveSession(email: String, name: String, phone: String) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_PHONE, phone)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String?  = prefs.getString(KEY_USER_NAME, null)
    fun getUserPhone(): String? = prefs.getString(KEY_USER_PHONE, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}