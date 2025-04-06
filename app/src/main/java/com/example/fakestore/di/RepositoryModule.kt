package com.example.fakestore.di

import com.example.fakestore.data.local.TokenManager
import com.example.fakestore.data.local.cart.CartDao
import com.example.fakestore.data.local.cart.CartRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao,
        tokenManager: TokenManager
    ): CartRepository {
        return CartRepository(cartDao, tokenManager)
    }
}
