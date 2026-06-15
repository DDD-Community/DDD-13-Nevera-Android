package com.anddd.nevera.data.di

import com.anddd.nevera.data.datasource.AppInfoRemoteDataSource
import com.anddd.nevera.data.datasource.FakeAppInfoRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAppInfoRemoteDataSource(impl: FakeAppInfoRemoteDataSource): AppInfoRemoteDataSource
}
