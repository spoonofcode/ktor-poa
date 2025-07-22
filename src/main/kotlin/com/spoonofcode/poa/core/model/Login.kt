package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val userId: Int,
    val jwtAccessToken: String,
    val jwtRefreshToken: String,
)