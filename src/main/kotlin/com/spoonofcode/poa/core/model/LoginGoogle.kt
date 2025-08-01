package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginGoogleRequest(
    val googleUserToken: String
)

@Serializable
data class LoginGoogle(
    val userId: Int,
    val jwtAccessToken: String,
    val jwtRefreshToken: String,
)