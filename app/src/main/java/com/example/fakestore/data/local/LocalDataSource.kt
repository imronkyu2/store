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
    // Product
    suspend fun saveProducts(userId: Int, products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }

    suspend fun getLocalProducts(userId: Int): List<ProductEntity> {
        return productDao.getAllProducts(userId)
    }

    // Category
    suspend fun saveCategories(userId: Int, categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun getAllCategories(userId: Int): List<CategoryEntity> {
        return categoryDao.getAllCategories(userId)
    }

    suspend fun updateCategoryCheckedStatus(userId: Int, categoryId: Int, isChecked: Boolean) {
        categoryDao.updateCategoryCheckedStatus(userId, categoryId, isChecked)
    }

    suspend fun clearCategorySelections(userId: Int) {
        categoryDao.clearAllSelections(userId)
    }
}
