package com.example.fakestore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fakestore.data.local.cart.CartDao
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.data.local.category.CategoryDao
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.local.product.ProductDao
import com.example.fakestore.data.local.product.ProductEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class, CartItem::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fakestore.db"
                )
                    .fallbackToDestructiveMigration() // Temporary for development
                    .build()
                    .also { instance = it }
            }
        }
    }
}