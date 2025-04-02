package com.example.fakestore.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.model.LoginRequest
import com.example.fakestore.ui.LoginState
import com.example.fakestore.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor
) {
    fun login(username: String, password: String) = flow {
        emit(LoginState.Loading)

        // Check network status before making the API call
        networkMonitor.networkStatus.collect { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                emit(LoginState.Error("No Internet Connection"))
                return@collect
            }

            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        // Save token to SharedPreferences
                        tokenManager.saveToken(it.token)
                        emit(LoginState.Success(it.token))
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        501 -> "Server error (501)"
                        502 -> "Bad gateway (502)"
                        else -> "Login failed"
                    }
                    emit(LoginState.Error(errorMessage))
                }
            } catch (e: IOException) {
                emit(LoginState.Error("No Internet Connection"))
            } catch (e: Exception) {
                emit(LoginState.Error("Unexpected Error"))
            }
        }
    }.flowOn(Dispatchers.IO)
}
