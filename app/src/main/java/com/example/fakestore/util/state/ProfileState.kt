package com.example.fakestore.util.state

import com.example.fakestore.data.local.prfile.User

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}