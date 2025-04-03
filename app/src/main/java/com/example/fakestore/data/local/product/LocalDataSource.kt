package com.example.fakestore.data.local

import com.example.fakestore.data.local.category.CategoryDao
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.local.product.ProductDao
import com.example.fakestore.data.local.product.ProductEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {
    // Product operations
    suspend fun saveProducts(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    suspend fun getLocalProducts(): List<ProductEntity> {
        return productDao.getAllProducts()
    }

    // Category operations
    suspend fun saveCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    suspend fun updateCategoryCheckedStatus(categoryId: Int, isChecked: Boolean) {
        categoryDao.updateCategoryCheckedStatus(categoryId, isChecked)
    }

    suspend fun clearCategorySelections() {
        // This will update all categories to isChecked = false
        categoryDao.clearAllSelections()
    }
}