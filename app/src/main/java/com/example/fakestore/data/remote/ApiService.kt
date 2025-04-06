package com.example.fakestore.data.remote

import com.example.fakestore.data.local.prfile.User
import com.example.fakestore.data.model.login.LoginRequest
import com.example.fakestore.data.model.login.LoginResponse
import com.example.fakestore.data.model.product.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<User>

}
