package com.spoonofcode.poa.feature.profile

import com.spoonofcode.poa.core.model.Profile

sealed class ProfileResult {
    data class Success(val profile: Profile) : ProfileResult()
    object UserNotFound : ProfileResult()
}