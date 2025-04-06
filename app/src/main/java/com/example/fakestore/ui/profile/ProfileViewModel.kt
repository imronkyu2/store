// ui/profile/ProfileViewModel.kt
package com.example.fakestore.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.remote.Repository
import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.util.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun getUserProfile() {
        val userId = tokenManager.getUserId()
        if (userId == null || userId == -1) {
            _profileState.value = ProfileState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            repository.getUser(userId).collect { state ->
                _profileState.value = state
            }
        }
    }
}