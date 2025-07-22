package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class RegisterResponse(
    val userId: Int,
    val jwtAccessToken: String,
    val jwtRefreshToken: String,
)