package com.example.fakestore.data.local.cart

import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.model.product.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val tokenManager: TokenManager
) {
    private suspend fun getUserId(): Int? = tokenManager.getUserId()

    suspend fun addToCart(product: Product) {
        val userId = getUserId() ?: return
        val existingItem = cartDao.getCartItemByProductId(userId, product.id)
        if (existingItem != null) {
            existingItem.quantity++
            cartDao.updateCartItem(existingItem)
        } else {
            cartDao.insertCartItem(
                CartItem(
                    userId = userId,
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
        val userId = getUserId() ?: return emptyList()
        return cartDao.getAllCartItems(userId)
    }

    suspend fun getCartItemCount(): Int {
        val userId = getUserId() ?: return 0
        return cartDao.getTotalItemCount(userId)
    }

    suspend fun removeFromCart(productId: Int) {
        val userId = getUserId() ?: return
        cartDao.deleteCartItem(userId, productId)
    }

    suspend fun clearCart() {
        val userId = getUserId() ?: return
        cartDao.clearCart(userId)
    }

    suspend fun updateCartItemQuantity(productId: Int, newQuantity: Int) {
        val userId = getUserId() ?: return
        val item = cartDao.getCartItemByProductId(userId, productId)
        item?.let {
            it.quantity = newQuantity
            cartDao.updateCartItem(it)
        }
    }

    suspend fun getCartItemQuantity(productId: Int): Int {
        val userId = getUserId() ?: return 0
        val cartItem = cartDao.getCartItemByProductId(userId, productId)
        return cartItem?.quantity ?: 0
    }

    fun getAllCartItems(): Flow<List<CartItem>> = flow {
        val userId = tokenManager.getUserId()
        if (userId != null) emitAll(cartDao.getAllCartItemsFlow(userId))
    }

    suspend fun updateCartItem(item: CartItem) {
        cartDao.updateCartItem(item)
    }
}
