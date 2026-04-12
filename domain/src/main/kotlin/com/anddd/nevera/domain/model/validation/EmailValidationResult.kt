package com.anddd.nevera.domain.model.validation

sealed interface EmailValidationResult {
    data object Valid : EmailValidationResult
    data object Empty : EmailValidationResult
    data object InvalidFormat : EmailValidationResult
}
