package com.anddd.nevera.domain.usecase.validation

import com.anddd.nevera.domain.model.validation.PasswordValidationError
import com.anddd.nevera.domain.model.validation.PasswordValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ValidatePasswordUseCaseTest {

    private val useCase = ValidatePasswordUseCase()

    @Test
    fun `한글만 포함된 비밀번호는 MissingLetter 에러를 반환한다`() {
        val result = useCase("가나다라마바1!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `영문 소문자를 포함하면 MissingLetter 에러가 없다`() {
        val result = useCase("password1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }

    @Test
    fun `영문 대문자를 포함하면 MissingLetter 에러가 없다`() {
        val result = useCase("Password1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }

    @Test
    fun `숫자만 포함된 비밀번호는 MissingLetter 에러를 반환한다`() {
        val result = useCase("12345678!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `특수문자만 포함된 비밀번호는 MissingLetter와 MissingDigit 에러를 반환한다`() {
        val result = useCase("!!!!!!!!!")

        assertTrue(result is PasswordValidationResult.Invalid)
        val errors = (result as PasswordValidationResult.Invalid).errors
        assertTrue(errors.contains(PasswordValidationError.MissingLetter))
        assertTrue(errors.contains(PasswordValidationError.MissingDigit))
    }

    @Test
    fun `영문자 숫자 특수문자를 모두 포함한 8자 비밀번호는 Valid를 반환한다`() {
        val result = useCase("Password1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }

    @Test
    fun `빈 비밀번호는 Empty를 반환한다`() {
        val result = useCase("")

        assertEquals(PasswordValidationResult.Empty, result)
    }

    @Test
    fun `공백만 포함된 비밀번호는 Empty를 반환한다`() {
        val result = useCase("   ")

        assertEquals(PasswordValidationResult.Empty, result)
    }

    @Test
    fun `한글과 영문이 혼합된 비밀번호는 MissingLetter 에러가 없다`() {
        val result = useCase("가나다abc1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }

    @Test
    fun `ASCII 범위 외 유니코드만 포함된 비밀번호는 MissingLetter 에러를 반환한다`() {
        val result = useCase("あ1234567!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `8자 미만 비밀번호는 TooShort 에러를 반환한다`() {
        val result = useCase("Ab1!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.TooShort(8)))
    }

    @Test
    fun `20자 초과 비밀번호는 TooLong 에러를 반환한다`() {
        val result = useCase("Password1!Password1!a")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.TooLong(20)))
    }
}
