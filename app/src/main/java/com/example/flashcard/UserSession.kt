package com.example.flashcard

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn

object UserSession {
    fun getCurrentUserId(context: Context): String? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null) return account.id
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("userId", null)
    }

    fun setCurrentUserId(context: Context, userId: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("userId", userId).apply()
    }
}
