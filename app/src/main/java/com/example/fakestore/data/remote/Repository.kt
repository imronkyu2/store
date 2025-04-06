package com.example.fakestore.data.remote

import com.auth0.android.jwt.JWT
import com.example.fakestore.data.local.LocalDataSource
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.local.mapper.mapToCategoryEntity
import com.example.fakestore.data.local.mapper.mapToDomainModel
import com.example.fakestore.data.local.mapper.mapToEntity
import com.example.fakestore.data.model.login.LoginRequest
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.util.NetworkMonitor
import com.example.fakestore.util.extensions.getErrorMessage
import com.example.fakestore.util.state.LoginState
import com.example.fakestore.util.state.ProductState
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

    /** AUTH */
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
                    val token = it.token
                    val jwt = JWT(token)
                    val userId = jwt.getClaim("sub").asInt()

                    if (userId != null) {
                        tokenManager.saveToken(token, userId)
                        emit(LoginState.Success(token))
                    } else {
                        emit(LoginState.Error("Login failed"))
                    }
                }
            } else {
                emit(LoginState.Error(response.getErrorMessage("Login failed")))
            }
        } catch (e: IOException) {
            emit(LoginState.Error("No Internet Connection"))
        } catch (e: Exception) {
            emit(LoginState.Error("Unexpected Error"))
        }
    }.flowOn(Dispatchers.IO)

    /** PRODUCT */
    fun getProducts() = flow {
        emit(ProductState.Loading)

        val userId = tokenManager.getUserId() ?: run {
            emit(ProductState.Error("User not logged in"))
            return@flow
        }

        try {
            val localProducts = localDataSource.getLocalProducts(userId)
            if (localProducts.isNotEmpty()) {
                emit(ProductState.Success(localProducts.mapToDomainModel()))
            }
        } catch (_: Exception) {}

        if (!networkMonitor.isNetworkAvailable()) {
            emit(ProductState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { products ->
                    saveProductsAndCategories(userId, products)
                    emit(ProductState.Success(products))
                } ?: emit(ProductState.Error("Empty response body"))
            } else {
                emit(ProductState.Error(response.getErrorMessage("Failed to load products")))
            }
        } catch (e: IOException) {
            emit(ProductState.Error("Network error: ${e.localizedMessage}"))
        } catch (e: HttpException) {
            emit(ProductState.Error("HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(ProductState.Error("Unexpected error: ${e.localizedMessage}"))
        }
    }.flowOn(Dispatchers.IO)

    /** PROFILE */
    fun getUserProfile() = flow {
        emit(ProfileState.Loading)

        val userId = tokenManager.getUserId() ?: run {
            emit(ProfileState.Error("User not logged in"))
            return@flow
        }

        tokenManager.getCachedProfile()?.let { cachedProfile ->
            emit(ProfileState.Success(cachedProfile))
            return@flow
        }

        if (!networkMonitor.isNetworkAvailable()) {
            emit(ProfileState.Error("No Internet Connection"))
            return@flow
        }

        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    tokenManager.saveProfile(user)
                    emit(ProfileState.Success(user))
                } ?: emit(ProfileState.Error("User not found"))
            } else {
                emit(ProfileState.Error(response.getErrorMessage("Failed to load profile")))
            }
        } catch (e: IOException) {
            emit(ProfileState.Error("Network error"))
        } catch (e: Exception) {
            emit(ProfileState.Error("Unexpected error"))
        }
    }.flowOn(Dispatchers.IO)

    /** CATEGORY */
    fun getCategories() = flow {
        val userId = tokenManager.getUserId() ?: run {
            emit(emptyList())
            return@flow
        }

        try {
            val categories = localDataSource.getAllCategories(userId)
            emit(categories)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateCategoryCheckedStatus(categoryId: Int, isChecked: Boolean) {
        val userId = tokenManager.getUserId() ?: return
        localDataSource.updateCategoryCheckedStatus(userId, categoryId, isChecked)
    }

    suspend fun clearAllCategorySelections() {
        val userId = tokenManager.getUserId() ?: return
        localDataSource.clearCategorySelections(userId)
    }

    /** LOCAL HELPERS */
    private suspend fun saveProductsAndCategories(userId: Int, products: List<Product>) {
        localDataSource.saveProducts(userId, products.mapToEntity(userId))
        localDataSource.saveCategories(userId, products.mapToCategoryEntity(userId))
    }
}
