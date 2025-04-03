package com.example.fakestore.data.local.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>

    @Query("UPDATE categories SET isChecked = :isChecked WHERE id = :categoryId")
    suspend fun updateCategoryCheckedStatus(categoryId: Int, isChecked: Boolean)

    @Query("UPDATE categories SET isChecked = 0") // Sets all isChecked to false
    suspend fun clearAllSelections()
}