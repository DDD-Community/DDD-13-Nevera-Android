package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.di.AuthInterceptorQualifier
import com.anddd.nevera.data.auth.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AuthModule {

    @Provides
    @Singleton
    @AuthInterceptorQualifier
    fun provideAuthInterceptor(interceptor: AuthInterceptor): Interceptor = interceptor
}
