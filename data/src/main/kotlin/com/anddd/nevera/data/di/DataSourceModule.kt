package com.anddd.nevera.data.di

import com.anddd.nevera.data.datasource.FakeSessionDataSource
import com.anddd.nevera.data.datasource.FakeSessionDataSourceImpl
import com.anddd.nevera.data.datasource.FakeUserDataSourceImpl
import com.anddd.nevera.data.datasource.LocalSessionDataSource
import com.anddd.nevera.data.datasource.LocalSessionDataSourceImpl
import com.anddd.nevera.data.datasource.LocalUserDataSource
import com.anddd.nevera.data.datasource.RemoteUserDataSource
import com.anddd.nevera.data.datasource.RemoteUserDataSourceImpl
import com.anddd.nevera.data.datasource.SessionDataSource
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

    @Binds
    @Singleton
    @LocalSessionDataSource
    abstract fun bindLocalSessionDataSource(impl: LocalSessionDataSourceImpl): SessionDataSource

    @Binds
    @Singleton
    @FakeSessionDataSource
    abstract fun bindFakeSessionDataSource(impl: FakeSessionDataSourceImpl): SessionDataSource
}
