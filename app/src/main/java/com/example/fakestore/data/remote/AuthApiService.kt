package com.example.fakestore.data.remote

import com.example.fakestore.data.model.LoginRequest
import com.example.fakestore.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
