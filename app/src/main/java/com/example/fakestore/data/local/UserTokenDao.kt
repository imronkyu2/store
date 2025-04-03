package com.example.fakestore.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakestore.data.model.login.UserToken

@Dao
interface UserTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: UserToken)

    @Query("SELECT * FROM user_token LIMIT 1")
    suspend fun getToken(): UserToken?
}
