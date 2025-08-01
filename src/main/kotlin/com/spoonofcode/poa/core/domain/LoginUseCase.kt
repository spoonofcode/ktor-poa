package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.base.utils.PasswordUtil
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.Login
import com.spoonofcode.poa.core.network.JwtConfig
import com.spoonofcode.poa.feature.login.login.LoginResult

class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordUtil: PasswordUtil,
) {
    suspend operator fun invoke(email: String, password: String): LoginResult {
        // verify email
        val existingUser = userRepository.readByEmail(email) ?: return LoginResult.UserNotFound

        // Verify password
        val hashedPassword = userRepository.readPassword(email)
        if (!passwordUtil.verifyPassword(password, hashedPassword)) {
            return LoginResult.InvalidCredentials
        }

        val jwtAccessToken = JwtConfig.createAccessToken(
            userId = existingUser.id.toString(),
            email = existingUser.email,
        )
        val jwtRefreshToken = JwtConfig.createRefreshToken(
            userId = existingUser.id.toString(),
            email = existingUser.email,
        )

        return LoginResult.Success(
            Login(
                userId = existingUser.id,
                jwtAccessToken = jwtAccessToken,
                jwtRefreshToken = jwtRefreshToken,
            )
        )
    }
}