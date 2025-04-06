package com.example.fakestore.data.local.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE userId = :userId")
    suspend fun getAllProducts(userId: Int): List<ProductEntity>

    @Query("DELETE FROM products WHERE userId = :userId")
    suspend fun clearAllProducts(userId: Int)

    @Query("SELECT DISTINCT category FROM products WHERE userId = :userId")
    suspend fun getAllCategories(userId: Int): List<String>
}
