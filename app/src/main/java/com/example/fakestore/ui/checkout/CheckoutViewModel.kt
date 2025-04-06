package com.example.fakestore.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.data.local.cart.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {
    val cartItems = cartRepository.getAllCartItems().asLiveData()

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
