package com.anddd.nevera.data.datasource

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CryptoHelper @Inject constructor(
    private val keyProvider: KeyProvider
) {

    fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keyProvider.getKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(iv + encrypted)
    }

    fun decrypt(value: String): String {
        val combined = Base64.getDecoder().decode(value)
        require(combined.size >= MIN_ENCRYPTED_LENGTH) {
            "Invalid encrypted data: too short to contain IV and auth tag"
        }
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, keyProvider.getKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    companion object {
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_BITS = 128
        private const val MIN_ENCRYPTED_LENGTH = GCM_IV_LENGTH + GCM_TAG_BITS / 8
    }
}
