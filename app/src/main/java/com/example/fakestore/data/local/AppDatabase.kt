package com.example.fakestore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakestore.data.model.UserToken

@Database(entities = [UserToken::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userTokenDao(): UserTokenDao
}
