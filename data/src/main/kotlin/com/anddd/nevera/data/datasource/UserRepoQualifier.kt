package com.anddd.nevera.data.datasource

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteUserDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalUserDataSource