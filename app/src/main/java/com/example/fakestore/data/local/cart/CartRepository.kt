// app/src/main/java/com/example/fakestore/data/repository/CartRepository.kt
package com.example.fakestore.data.local.cart

import com.example.fakestore.data.model.product.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao
) {
    suspend fun addToCart(product: Product) {
        val existingItem = cartDao.getCartItemByProductId(product.id)
        if (existingItem != null) {
            existingItem.quantity++
            cartDao.updateCartItem(existingItem)
        } else {
            cartDao.insertCartItem(
                CartItem(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    image = product.image,
                    quantity = 1
                )
            )
        }
    }

    suspend fun getCartItems(): List<CartItem> {
        return cartDao.getAllCartItems()
    }

    suspend fun getCartItemCount(): Int {
        return cartDao.getAllCartItems().sumOf { it.quantity }
    }

    suspend fun removeFromCart(productId: Int) {
        cartDao.deleteCartItem(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    suspend fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        val item = cartDao.getCartItemByProductId(productId)
        item?.let {
            it.quantity = newQuantity
            cartDao.updateCartItem(it)
        }
    }

    suspend fun getCartItemQuantity(productId: Int): Int {
        val cartItem = cartDao.getCartItemByProductId(productId)
        return cartItem?.quantity ?: 0
    }
}