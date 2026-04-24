package com.anddd.nevera.data.di

import com.anddd.nevera.data.repository.FcmTokenRepositoryImpl
import com.anddd.nevera.data.repository.UserRepositoryImpl
import com.anddd.nevera.data.repository.TokenRepositoryImpl
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.UserRepository
import com.anddd.nevera.domain.repository.TokenRepository
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
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(impl: FcmTokenRepositoryImpl): FcmTokenRepository
}
