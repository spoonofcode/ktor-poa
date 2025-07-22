package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.base.utils.PasswordUtil
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.RegisterResponse
import com.spoonofcode.poa.core.model.UserRequest
import com.spoonofcode.poa.core.network.JwtConfig
import com.spoonofcode.poa.feature.login.register.RegisterResult

class RegisterUseCase(
    private val userRepository: UserRepository,
    private val passwordUtil: PasswordUtil,
) {
    suspend operator fun invoke(
        userRequest: UserRequest
    ): RegisterResult {
        val existingUser = userRepository.readByEmail(userRequest.email)

        if (existingUser != null) {
            return RegisterResult.UserAlreadyExist
        }

        val hashedPassword = passwordUtil.hashPassword(userRequest.password!!)

        val newUser = userRepository.create(
            userRequest.copy(password = hashedPassword)
        )

        val jwtAccessToken = JwtConfig.createAccessToken(
            userId = newUser.id.toString(),
            email = newUser.email,
        )
        val jwtRefreshToken = JwtConfig.createRefreshToken(
            userId = newUser.id.toString(),
            email = newUser.email,
        )

        return RegisterResult.Success(
            RegisterResponse(
                userId = newUser.id,
                jwtAccessToken = jwtAccessToken,
                jwtRefreshToken = jwtRefreshToken
            )
        )
    }
}