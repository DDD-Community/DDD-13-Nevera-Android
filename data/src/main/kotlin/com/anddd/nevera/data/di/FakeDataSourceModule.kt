package com.anddd.nevera.data.di

import com.anddd.nevera.data.datasource.AppInfoRemoteDataSource
import com.anddd.nevera.data.datasource.FakeAppInfoRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TODO 실제 API 연동 시, 제거
@Module
@InstallIn(SingletonComponent::class)
internal abstract class FakeDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAppInfoRemoteDataSource(impl: FakeAppInfoRemoteDataSource): AppInfoRemoteDataSource
}
