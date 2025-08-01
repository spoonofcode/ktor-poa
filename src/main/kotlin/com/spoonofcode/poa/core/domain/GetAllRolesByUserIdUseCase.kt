package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.RoleRepository
import com.spoonofcode.poa.core.model.Role

class GetAllRolesByUserIdUseCase(
    private val roleRepository: RoleRepository,
) {
    suspend operator fun invoke(userId: Int): List<Role> = roleRepository.readAllRolesByUserId(userId = userId)
}