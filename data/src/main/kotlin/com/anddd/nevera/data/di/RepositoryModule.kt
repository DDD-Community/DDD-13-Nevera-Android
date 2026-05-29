package com.anddd.nevera.data.di

import com.anddd.nevera.data.repository.AppInfoRepositoryImpl
import com.anddd.nevera.data.repository.AuthRepositoryImpl
import com.anddd.nevera.data.repository.FcmTokenRepositoryImpl
import com.anddd.nevera.data.repository.HomeRepositoryImpl
import com.anddd.nevera.data.repository.IngredientRepositoryImpl
import com.anddd.nevera.data.repository.TokenRepositoryImpl
import com.anddd.nevera.data.repository.UserRepositoryImpl
import com.anddd.nevera.data.repository.WishRepositoryImpl
import com.anddd.nevera.domain.repository.AppInfoRepository
import com.anddd.nevera.domain.repository.AuthRepository
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.HomeRepository
import com.anddd.nevera.domain.repository.IngredientRepository
import com.anddd.nevera.domain.repository.TokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import com.anddd.nevera.domain.repository.WishRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: TokenRepositoryImpl): TokenRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(impl: FcmTokenRepositoryImpl): FcmTokenRepository

    @Binds
    @Singleton
    abstract fun bindAppInfoRepository(impl: AppInfoRepositoryImpl): AppInfoRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(impl: IngredientRepositoryImpl): IngredientRepository

    @Binds
    @Singleton
    abstract fun bindWishRepository(impl: WishRepositoryImpl): WishRepository
}
