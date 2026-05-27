package com.anddd.nevera.data.di

import com.anddd.nevera.data.datasource.AndroidKeyStoreProvider
import com.anddd.nevera.data.datasource.AuthRemoteDataSource
import com.anddd.nevera.data.datasource.AuthRemoteDataSourceImpl
import com.anddd.nevera.data.datasource.FcmTokenLocalDataSource
import com.anddd.nevera.data.datasource.FcmTokenLocalDataSourceImpl
import com.anddd.nevera.data.datasource.FcmTokenRemoteDataSource
import com.anddd.nevera.data.datasource.FcmTokenRemoteDataSourceImpl
import com.anddd.nevera.data.datasource.FirebaseFcmTokenProvider
import com.anddd.nevera.data.datasource.KeyProvider
import com.anddd.nevera.data.datasource.RefreshDataSource
import com.anddd.nevera.data.datasource.RefreshDataSourceImpl
import com.anddd.nevera.data.datasource.TokenDataSource
import com.anddd.nevera.data.datasource.TokenDataSourceImpl
import com.anddd.nevera.data.datasource.UserRemoteDataSource
import com.anddd.nevera.data.datasource.UserRemoteDataSourceImpl
import com.anddd.nevera.data.datasource.HomeRemoteDataSource
import com.anddd.nevera.data.datasource.HomeRemoteDataSourceImpl
import com.anddd.nevera.data.datasource.IngredientRemoteDataSource
import com.anddd.nevera.data.datasource.IngredientRemoteDataSourceImpl
import com.anddd.nevera.domain.repository.FcmTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindKeyProvider(impl: AndroidKeyStoreProvider): KeyProvider

    @Binds
    @Singleton
    abstract fun bindTokenDataSource(impl: TokenDataSourceImpl): TokenDataSource

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(impl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(impl: UserRemoteDataSourceImpl): UserRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindRefreshDataSource(impl: RefreshDataSourceImpl): RefreshDataSource

    @Binds
    @Singleton
    abstract fun bindFcmTokenLocalDataSource(impl: FcmTokenLocalDataSourceImpl): FcmTokenLocalDataSource

    @Binds
    @Singleton
    abstract fun bindFcmTokenRemoteDataSource(impl: FcmTokenRemoteDataSourceImpl): FcmTokenRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFcmTokenProvider(impl: FirebaseFcmTokenProvider): FcmTokenProvider

    @Binds
    @Singleton
    abstract fun bindHomeRemoteDataSource(impl: HomeRemoteDataSourceImpl): HomeRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindIngredientRemoteDataSource(impl: IngredientRemoteDataSourceImpl): IngredientRemoteDataSource
}
