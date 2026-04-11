package com.anddd.nevera.data.datasource

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class CryptoHelperTest {

    private lateinit var cryptoHelper: CryptoHelper

    @BeforeEach
    fun setUp() {
        val fakeKeyProvider = FakeKeyProvider()
        cryptoHelper = CryptoHelper(fakeKeyProvider)
    }

    @Test
    fun `encrypt - 평문과 다른 값을 반환한다`() {
        val plain = "access_token_value"

        val encrypted = cryptoHelper.encrypt(plain)

        assertNotEquals(plain, encrypted)
    }

    @Test
    fun `decrypt - encrypt 한 값을 원래 평문으로 복원한다`() {
        val plain = "access_token_value"

        val decrypted = cryptoHelper.decrypt(cryptoHelper.encrypt(plain))

        assertEquals(plain, decrypted)
    }

    @Test
    fun `encrypt - 동일한 입력이라도 매번 다른 암호문을 생성한다`() {
        val plain = "access_token_value"

        val encrypted1 = cryptoHelper.encrypt(plain)
        val encrypted2 = cryptoHelper.encrypt(plain)

        assertNotEquals(encrypted1, encrypted2)
    }

    @Test
    fun `decrypt - 각각 다르게 암호화된 값이 동일한 평문으로 복원된다`() {
        val plain = "access_token_value"
        val encrypted1 = cryptoHelper.encrypt(plain)
        val encrypted2 = cryptoHelper.encrypt(plain)

        assertEquals(cryptoHelper.decrypt(encrypted1), cryptoHelper.decrypt(encrypted2))
    }

    @Test
    fun `encrypt, decrypt - 한글 문자열을 정상적으로 처리한다`() {
        val plain = "한글_토큰_값"

        val decrypted = cryptoHelper.decrypt(cryptoHelper.encrypt(plain))

        assertEquals(plain, decrypted)
    }

    @Test
    fun `encrypt, decrypt - 빈 문자열을 정상적으로 처리한다`() {
        val plain = ""

        val decrypted = cryptoHelper.decrypt(cryptoHelper.encrypt(plain))

        assertEquals(plain, decrypted)
    }

    @Test
    fun `decrypt - 손상된 암호문은 예외를 발생시킨다`() {
        val tampered = "invaliddataXXXXXXXXXXXXXXXXXXXXXXXX"

        assertThrows(Exception::class.java) {
            cryptoHelper.decrypt(tampered)
        }
    }

    @Test
    fun `decrypt - 다른 키로 암호화된 값은 복호화에 실패한다`() {
        val plain = "access_token_value"
        val otherCryptoHelper = CryptoHelper(FakeKeyProvider())

        val encryptedByOther = otherCryptoHelper.encrypt(plain)

        assertThrows(Exception::class.java) {
            cryptoHelper.decrypt(encryptedByOther)
        }
    }
}

private class FakeKeyProvider : KeyProvider {
    private val key: SecretKey = KeyGenerator.getInstance("AES").apply {
        init(256)
    }.generateKey()

    override fun getKey(): SecretKey = key
}
