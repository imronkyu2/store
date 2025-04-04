package com.example.fakestore.ui.keranjang

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.data.local.cart.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeranjangViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val cartItems: LiveData<List<CartItem>> = cartRepository.getAllCartItems().asLiveData()

    fun updateCartItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.updateCartItem(item)
        }
    }

    fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item.productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}