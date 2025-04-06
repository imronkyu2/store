package com.example.fakestore.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.fakestore.data.local.prfile.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveToken(token: String, userId: Int) {
        sharedPreferences.edit {
            putString("user_token", token)
            putInt("user_id", userId)
        }
    }

    fun saveProfile(user: User) {
        sharedPreferences.edit {
            putString("user_profile", gson.toJson(user))
        }
    }

    fun getCachedProfile(): User? {
        val profileJson = sharedPreferences.getString("user_profile", null)
        return profileJson?.let { gson.fromJson(it, User::class.java) }
    }

    fun getToken(): String? = sharedPreferences.getString("user_token", null)

    fun getUserId(): Int? = sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }
}