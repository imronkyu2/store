package com.example.fakestore.data.local.mapper

import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.local.product.ProductEntity
import com.example.fakestore.data.model.product.Product
import com.example.fakestore.data.model.product.Rating

fun List<Product>.mapToEntity(userId: Int): List<ProductEntity> {
    return this.map {
        ProductEntity(
            id = it.id,
            userId = userId,
            title = it.title,
            price = it.price,
            description = it.description,
            category = it.category,
            image = it.image,
            rate = it.rating.rate,
            count = it.rating.count
        )
    }
}

fun List<ProductEntity>.mapToDomainModel(): List<Product> {
    return this.map {
        Product(
            id = it.id,
            title = it.title,
            price = it.price,
            description = it.description,
            category = it.category,
            image = it.image,
            rating = Rating(rate = it.rate, count = it.count)
        )
    }
}

fun List<Product>.mapToCategoryEntity(userId: Int): List<CategoryEntity> {
    return this.distinctBy { it.category }.mapIndexed { index, product ->
        CategoryEntity(
            id = index,
            userId = userId,
            name = product.category,
            isChecked = false
        )
    }
}
