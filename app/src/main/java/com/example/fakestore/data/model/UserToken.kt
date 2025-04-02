package com.example.fakestore.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_token")
data class UserToken(
    @PrimaryKey val id: Int = 0,
    val token: String
)
