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
    fun `빈 문자열은 Empty를 반환한다`() {
        val result = useCase("")

        assertEquals(PasswordValidationResult.Empty, result)
    }

    @Test
    fun `공백 문자열은 Empty를 반환한다`() {
        val result = useCase("   ")

        assertEquals(PasswordValidationResult.Empty, result)
    }

    @Test
    fun `8자 미만이면 TooShort 에러를 반환한다`() {
        val result = useCase("Ab1!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.TooShort(8)))
    }

    @Test
    fun `20자 초과이면 TooLong 에러를 반환한다`() {
        val result = useCase("Password1!Password1!a")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.TooLong(20)))
    }

    @Test
    fun `한글이 포함되면 ContainsInvalidCharacter 에러를 반환한다`() {
        val result = useCase("Password1!가")

        assertTrue(result is PasswordValidationResult.Invalid)
        val errors = (result as PasswordValidationResult.Invalid).errors
        assertTrue(errors.contains(PasswordValidationError.ContainsInvalidCharacter))
        assertFalse(errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `한글만 입력된 비밀번호는 ContainsInvalidCharacter와 MissingLetter 에러를 반환한다`() {
        val result = useCase("가나다라마바1!")

        assertTrue(result is PasswordValidationResult.Invalid)
        val errors = (result as PasswordValidationResult.Invalid).errors
        assertTrue(errors.contains(PasswordValidationError.ContainsInvalidCharacter))
        assertTrue(errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `영문자가 없으면 MissingLetter 에러를 반환한다`() {
        val result = useCase("12345678!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingLetter))
    }

    @Test
    fun `숫자가 없으면 MissingDigit 에러를 반환한다`() {
        val result = useCase("Password!!")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingDigit))
    }

    @Test
    fun `특수문자가 없으면 MissingSpecialChar 에러를 반환한다`() {
        val result = useCase("Password11")

        assertTrue(result is PasswordValidationResult.Invalid)
        assertTrue((result as PasswordValidationResult.Invalid).errors.contains(PasswordValidationError.MissingSpecialChar))
    }

    @Test
    fun `특수문자만 있으면 MissingLetter와 MissingDigit 에러를 반환한다`() {
        val result = useCase("!!!!!!!!!")

        assertTrue(result is PasswordValidationResult.Invalid)
        val errors = (result as PasswordValidationResult.Invalid).errors
        assertTrue(errors.contains(PasswordValidationError.MissingLetter))
        assertTrue(errors.contains(PasswordValidationError.MissingDigit))
    }

    @Test
    fun `영문 소문자, 숫자, 특수문자를 포함한 비밀번호는 Valid를 반환한다`() {
        val result = useCase("password1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }

    @Test
    fun `영문 대문자, 숫자, 특수문자를 포함한 비밀번호는 Valid를 반환한다`() {
        val result = useCase("Password1!")

        assertEquals(PasswordValidationResult.Valid, result)
    }
}
