package com.anddd.nevera.core.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RefreshApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OcrExtractOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OcrExtractRetrofit

