package com.example.fakestore.di

import android.content.Context
import com.example.fakestore.data.local.AppDatabase
import com.example.fakestore.data.local.cart.CartDao
import com.example.fakestore.data.local.category.CategoryDao
import com.example.fakestore.data.local.product.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideCartDao(database: AppDatabase): CartDao = database.cartDao()
}