package com.anddd.nevera.domain.model.user

import com.anddd.nevera.domain.model.common.CommonError

sealed interface UpdateNicknameError {
    data object InvalidNickname : UpdateNicknameError
    data class Common(val error: CommonError) : UpdateNicknameError
}
