package com.anddd.nevera.data.datasource

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDbTestDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDbTestDataSource
