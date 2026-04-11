package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.auth.SessionEventBus
import com.anddd.nevera.core.network.auth.SessionEventBusImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindSessionEventBus(impl: SessionEventBusImpl): SessionEventBus
}
