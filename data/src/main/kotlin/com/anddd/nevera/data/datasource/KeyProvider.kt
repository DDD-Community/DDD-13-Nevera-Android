package com.anddd.nevera.data.datasource

import javax.crypto.SecretKey

internal interface KeyProvider {
    fun getKey(): SecretKey
}
