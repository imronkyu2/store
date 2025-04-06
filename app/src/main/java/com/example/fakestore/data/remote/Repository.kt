package com.example.fakestore.data.remote

import com.auth0.android.jwt.JWT
import com.example.fakestore.data.local.LocalDataSource
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.local.prfile.User
import com.example.fakestore.data.local.product.ProductEntity
import com.example.fakestore.data.model.login.LoginRequest
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.data.model.product.Rating
import com.example.fakestore.util.state.LoginState
import com.example.fakestore.util.state.ProductState
import com.example.fakestore.util.NetworkMonitor
import com.example.fakestore.util.state.ProfileState
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
    private val networkMonitor: NetworkMonitor,
    private val localDataSource: LocalDataSource
) {
    fun login(username: String, password: String) = flow {
        emit(LoginState.Loading)

        if (!networkMonitor.isNetworkAvailable()) {
            emit(LoginState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                response.body()?.let {
                   // Save token to SharedPreferences
                    val token = it.token

                    // Decode JWT untuk mendapatkan nilai `sub`
                    val jwt = JWT(token)
                    val userId = jwt.getClaim("sub").asInt() // Mengambil sub sebagai Int

                    if (userId != null) {
                        tokenManager.saveToken(token, userId)
                        emit(LoginState.Success(it.token))
                    } else {
                        emit(LoginState.Error("Login failed"))
                    }

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

        // Check local first
        try {
            val localProducts = localDataSource.getLocalProducts()
            if (localProducts.isNotEmpty()) {
                emit(ProductState.Success(localProducts.mapToDomainModel()))
            }
        } catch (e: Exception) {}

        if (!networkMonitor.isNetworkAvailable()) {
            emit(ProductState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { products ->
                    saveProductsAndCategories(products)
                    emit(ProductState.Success(products))
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

    fun getCategories() = flow {
        try {
            val categories = localDataSource.getAllCategories()
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateCategoryCheckedStatus(categoryId: Int, isChecked: Boolean) {
        localDataSource.updateCategoryCheckedStatus(categoryId, isChecked)
    }

    private suspend fun saveProductsAndCategories(products: List<Product>) {
        // Save products
        localDataSource.saveProducts(products.mapToEntity())

        // Save categories
        val categories = products.distinctBy { it.category }.mapIndexed { index, product ->
            CategoryEntity(
                id = index,
                name = product.category,
                isChecked = false
            )
        }
        localDataSource.saveCategories(categories)
    }

    // tambahkan di Repository.kt
    fun getUser(userId: Int) = flow {
        emit(ProfileState.Loading)

        if (!networkMonitor.isNetworkAvailable()) {
            emit(ProfileState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(ProfileState.Success(it))
                } ?: emit(ProfileState.Error("User not found"))
            } else {
                emit(ProfileState.Error("Failed to load profile: ${response.code()}"))
            }
        } catch (e: IOException) {
            emit(ProfileState.Error("Network error"))
        } catch (e: Exception) {
            emit(ProfileState.Error("Unexpected error"))
        }
    }.flowOn(Dispatchers.IO)

    // Tambahkan sealed class untuk state


    // Extension functions untuk konversi model
    private fun List<Product>.mapToEntity(): List<ProductEntity> {
        return this.map {
            ProductEntity(
                id = it.id,
                title = it.title,
                price = it.price,
                description = it.description,
                category = it.category,
                image = it.image,
                rate = it.rating.rate,
                count = it.rating.count
            )
        }
    }

    private fun List<ProductEntity>.mapToDomainModel(): List<Product> {
        return this.map {
            Product(
                id = it.id,
                title = it.title,
                price = it.price,
                description = it.description,
                category = it.category,
                image = it.image,
                rating = Rating(rate = it.rate, count = it.count)
            )
        }
    }


    suspend fun clearAllCategorySelections() {
        localDataSource.clearCategorySelections()
    }

}
