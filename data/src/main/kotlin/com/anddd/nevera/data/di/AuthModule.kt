package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.auth.SessionEventBus
import com.anddd.nevera.core.network.auth.SessionEventBusImpl
import com.anddd.nevera.core.network.auth.TokenProvider
import com.anddd.nevera.data.auth.TokenProviderAdapter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// @Provides(object)와 @Binds(abstract fun)는 같은 Hilt 모듈 클래스에 공존할 수 없으므로 모듈을 분리한다.
@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindTokenProvider(impl: TokenProviderAdapter): TokenProvider

    @Binds
    @Singleton
    abstract fun bindSessionEventBus(impl: SessionEventBusImpl): SessionEventBus

}