package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.User

class GetAllUsersByRoleIdUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(roleId: Int): List<User> = userRepository.readAllUserByRoleId(roleId = roleId)
}