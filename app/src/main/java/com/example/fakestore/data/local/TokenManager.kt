package com.example.fakestore.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// TokenManager.kt
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveToken(token: String, userId: Int) {
        sharedPreferences.edit {
            putString("user_token", token)
            putInt("user_id", userId) // Simpan sebagai Int
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("user_token", null)
    }

    fun getUserId(): Int? {
        return if (sharedPreferences.contains("user_id")) {
            sharedPreferences.getInt("user_id", -1)
        } else {
            null
        }
    }

    fun clearToken() {
        sharedPreferences.edit {
            remove("user_token")
            remove("user_id")
        }
    }
}
