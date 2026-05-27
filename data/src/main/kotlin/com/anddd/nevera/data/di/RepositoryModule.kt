package com.anddd.nevera.data.di

import com.anddd.nevera.data.repository.AppInfoRepositoryImpl
import com.anddd.nevera.data.repository.FcmTokenRepositoryImpl
import com.anddd.nevera.data.repository.AuthRepositoryImpl
import com.anddd.nevera.data.repository.TokenRepositoryImpl
import com.anddd.nevera.domain.repository.AppInfoRepository
import com.anddd.nevera.domain.repository.FcmTokenRepository
import com.anddd.nevera.domain.repository.AuthRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(impl: FcmTokenRepositoryImpl): FcmTokenRepository

    @Binds
    @Singleton
    abstract fun bindAppInfoRepository(impl: AppInfoRepositoryImpl): AppInfoRepository
}
