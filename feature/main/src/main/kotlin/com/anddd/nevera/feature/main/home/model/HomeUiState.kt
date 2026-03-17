package com.anddd.nevera.feature.main.home.model

import com.anddd.nevera.domain.model.User

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val user: User) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
