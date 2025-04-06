package com.example.fakestore.data.local.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getAllCartItems(userId: Int): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun getCartItemByProductId(userId: Int, productId: Int): CartItem?

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun deleteCartItem(userId: Int, productId: Int)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    suspend fun getTotalItemCount(userId: Int): Int

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getAllCartItemsFlow(userId: Int): Flow<List<CartItem>>
}
