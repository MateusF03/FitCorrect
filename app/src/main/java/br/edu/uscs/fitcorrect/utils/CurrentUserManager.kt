package br.edu.uscs.fitcorrect.utils

import android.content.Context
import androidx.core.content.edit

object CurrentUserManager {
    private const val PREFS_NAME = "fitcorrect_prefs"
    private const val KEY_CURRENT_USER_ID = "current_user_id"

    fun saveCurrentUserId(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_CURRENT_USER_ID, userId) }
    }

    fun getCurrentUserId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENT_USER_ID, null)
    }
}