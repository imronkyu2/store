package com.example.fakestore.util.extensions

import retrofit2.Response

fun <T> Response<T>.getErrorMessage(default: String = "Something went wrong"): String {
    return when (this.code()) {
        400 -> "Bad request (400)"
        401 -> "Unauthorized (401)"
        403 -> "Forbidden (403)"
        404 -> "Not found (404)"
        500 -> "Internal server error (500)"
        501 -> "Server error (501)"
        502 -> "Bad gateway (502)"
        503 -> "Service unavailable (503)"
        else -> "$default (${this.code()})"
    }
}
