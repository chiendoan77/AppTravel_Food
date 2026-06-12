package com.example.apptravelfood.core.session

import android.content.Context

class SessionManager(
    context: Context
) {
    private val prefs = context.getSharedPreferences(
        "travel_food_session",
        Context.MODE_PRIVATE
    )

    fun saveLogin(userId: Long) {
        prefs.edit()
            .putLong("logged_user_id", userId)
            .apply()
    }

    fun getLoggedUserId(): Long? {
        val id = prefs.getLong("logged_user_id", -1L)
        return if (id == -1L) null else id
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}