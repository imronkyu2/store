package com.example.fakestore.data.local.cart

import androidx.room.Entity
import androidx.room.PrimaryKey

// data/local/cart/CartItem.kt
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    var quantity: Int = 1
) {
    fun getTotalPrice(): Double = price * quantity
}