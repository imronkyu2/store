package com.example.fakestore.ui.login

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
