package com.example.fakestore.data.remote

import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.model.login.LoginRequest
import com.example.fakestore.ui.login.LoginState
import com.example.fakestore.ui.main.product.ProductState
import com.example.fakestore.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Repository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor
) {
    fun login(username: String, password: String) = flow {
        emit(LoginState.Loading)

        if (!networkMonitor.isNetworkAvailable()) { // Untuk Opsi 1
            // if (!networkMonitor.networkStatus.value) { // Untuk Opsi 2
            emit(LoginState.Error("No Internet Connection"))
            return@flow
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
    }.flowOn(Dispatchers.IO)



    fun getProducts() = flow {
        emit(ProductState.Loading)

        if (!networkMonitor.isNetworkAvailable()) {
            emit(ProductState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { products ->
                    if (products.isEmpty()) {
                        emit(ProductState.Error("No products available"))
                    } else {
                        emit(ProductState.Success(products))
                    }
                } ?: emit(ProductState.Error("Empty response body"))
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Products not found (404)"
                    500 -> "Server error (500)"
                    502 -> "Bad gateway (502)"
                    503 -> "Service unavailable (503)"
                    else -> "Failed to load products (${response.code()})"
                }
                emit(ProductState.Error(errorMessage))
            }
        } catch (e: IOException) {
            emit(ProductState.Error("Network error: ${e.localizedMessage}"))
        } catch (e: HttpException) {
            emit(ProductState.Error("HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(ProductState.Error("Unexpected error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)
}
