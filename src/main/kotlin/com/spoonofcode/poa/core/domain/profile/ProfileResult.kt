package com.spoonofcode.poa.core.domain.profile

import com.spoonofcode.poa.core.model.Profile

sealed class ProfileResult {
    data class Success(val profile: Profile) : ProfileResult()
    object NotFound : ProfileResult()
}