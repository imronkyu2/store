package com.example.fakestore.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveToken(token: String, userId: Int) {
        sharedPreferences.edit() { putString("user_token", token) }
        sharedPreferences.edit(){ putString("user_token", userId.toString())}
    }

    fun getToken(): String? {
        return this.sharedPreferences.getString("user_token", null)
    }
    
    fun getUserId(): String? {
        return this.sharedPreferences.getString("user_token", null)
    }

    fun clearToken() {
        sharedPreferences.edit() { remove("user_token") }
    }

}
