package com.example.fakestore.data.local.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE userId = :userId")
    suspend fun getAllCategories(userId: Int): List<CategoryEntity>

    @Query("UPDATE categories SET isChecked = :isChecked WHERE id = :categoryId AND userId = :userId")
    suspend fun updateCategoryCheckedStatus(userId: Int, categoryId: Int, isChecked: Boolean)

    @Query("UPDATE categories SET isChecked = 0 WHERE userId = :userId")
    suspend fun clearAllSelections(userId: Int)
}
