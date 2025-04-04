package com.example.fakestore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fakestore.data.local.cart.CartDao
import com.example.fakestore.data.local.cart.CartItem
import com.example.fakestore.data.local.category.CategoryDao
import com.example.fakestore.data.local.category.CategoryEntity
import com.example.fakestore.data.local.product.ProductDao
import com.example.fakestore.data.local.product.ProductEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class, CartItem::class],
    version = 3,
    exportSchema = false  // Add this to avoid schema export unless needed
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // Double-checked locking pattern
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fakestore.db"
            )
                .fallbackToDestructiveMigration() // Consider removing in production
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // You can add database seeding here if needed
                    }
                })
                .build()
        }
    }
}