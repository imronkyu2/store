package com.example.fakestore.data.local.product

import androidx.room.Entity
import androidx.room.PrimaryKey

// ProductEntity.kt
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rate: Double,
    val count: Int
)