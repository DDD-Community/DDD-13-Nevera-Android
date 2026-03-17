package com.anddd.nevera.data.di

import com.anddd.nevera.data.datasource.FakeUserDataSourceImpl
import com.anddd.nevera.data.datasource.LocalUserDataSource
import com.anddd.nevera.data.datasource.RemoteUserDataSourceImpl
import com.anddd.nevera.data.datasource.RemoteUserDataSource
import com.anddd.nevera.data.datasource.UserDataSource
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
    @LocalUserDataSource
    abstract fun bindLocalUserDataSource(impl: FakeUserDataSourceImpl): UserDataSource

    @Binds
    @Singleton
    @RemoteUserDataSource
    abstract fun bindRemoteUserDataSource(impl: RemoteUserDataSourceImpl): UserDataSource
}
