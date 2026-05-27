package com.anddd.nevera.domain.repository

import com.anddd.nevera.core.common.NeveraResult
import com.anddd.nevera.domain.model.user.Profile
import com.anddd.nevera.domain.model.user.ProfileError

interface UserRepository {
    suspend fun getProfile(): NeveraResult<Profile, ProfileError>
}
