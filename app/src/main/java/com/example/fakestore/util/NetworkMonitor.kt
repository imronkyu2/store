package com.example.fakestore.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    private val context: Context
) {
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}