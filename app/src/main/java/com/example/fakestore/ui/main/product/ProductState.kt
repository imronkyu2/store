package com.example.fakestore.ui.main.product

import com.example.fakestore.data.model.product.Product

sealed class ProductState {
    object Loading : ProductState()
    data class Success(val products: List<Product>) : ProductState()
    data class Error(val message: String) : ProductState()
}