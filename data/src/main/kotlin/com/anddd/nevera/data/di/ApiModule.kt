package com.anddd.nevera.data.di

import com.anddd.nevera.core.network.di.AuthOkHttpClient
import com.anddd.nevera.data.api.AuthApi
import com.anddd.nevera.data.api.GoogleLoginApi
import com.anddd.nevera.data.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(@AuthOkHttpClient retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideGoogleLoginApi(retrofit: Retrofit): GoogleLoginApi =
        retrofit.create(GoogleLoginApi::class.java)
}
