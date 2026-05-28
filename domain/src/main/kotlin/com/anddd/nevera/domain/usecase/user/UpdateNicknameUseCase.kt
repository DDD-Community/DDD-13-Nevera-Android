package com.anddd.nevera.domain.usecase.user

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.UpdateNicknameError
import com.anddd.nevera.domain.repository.UserRepository
import javax.inject.Inject

class UpdateNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(nickname: String): NeveraResult<Profile, UpdateNicknameError> {
        return userRepository.updateNickname(nickname)
    }
}
