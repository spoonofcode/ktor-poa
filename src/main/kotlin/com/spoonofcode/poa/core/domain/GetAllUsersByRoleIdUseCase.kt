package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.UserResponse

class GetAllUsersByRoleIdUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(roleId: Int): List<UserResponse> = userRepository.readAllUserByRoleId(roleId = roleId)
}