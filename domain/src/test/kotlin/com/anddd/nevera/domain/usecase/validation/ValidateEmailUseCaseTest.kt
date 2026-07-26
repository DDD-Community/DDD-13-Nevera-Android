package com.anddd.nevera.domain.usecase.validation

import com.anddd.nevera.domain.model.validation.EmailValidationResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ValidateEmailUseCaseTest {

    private val useCase = ValidateEmailUseCase()

    @Test
    fun `빈 문자열은 Empty를 반환한다`() {
        val result = useCase("")

        assertEquals(EmailValidationResult.Empty, result)
    }

    @Test
    fun `공백 문자열은 Empty를 반환한다`() {
        val result = useCase("   ")

        assertEquals(EmailValidationResult.Empty, result)
    }

    @Test
    fun `골뱅이가 없으면 InvalidFormat을 반환한다`() {
        val result = useCase("nevera.example.com")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `도메인에 점이 없으면 InvalidFormat을 반환한다`() {
        val result = useCase("user@example")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `최상위 도메인이 한 글자면 InvalidFormat을 반환한다`() {
        val result = useCase("user@example.c")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `골뱅이 앞이 비어 있으면 InvalidFormat을 반환한다`() {
        val result = useCase("@example.com")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `한글이 포함되면 InvalidFormat을 반환한다`() {
        val result = useCase("사용자@example.com")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `공백이 포함되면 InvalidFormat을 반환한다`() {
        val result = useCase("user name@example.com")

        assertEquals(EmailValidationResult.InvalidFormat, result)
    }

    @Test
    fun `일반적인 이메일은 Valid를 반환한다`() {
        val result = useCase("user@example.com")

        assertEquals(EmailValidationResult.Valid, result)
    }

    @Test
    fun `점과 하이픈 등 허용된 특수문자가 있는 이메일은 Valid를 반환한다`() {
        val result = useCase("first.last+tag_1%x-y@sub-domain.example.co.kr")

        assertEquals(EmailValidationResult.Valid, result)
    }
}
