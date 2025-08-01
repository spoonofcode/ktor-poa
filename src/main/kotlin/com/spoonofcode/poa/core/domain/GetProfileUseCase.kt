package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.Profile
import com.spoonofcode.poa.core.model.User
import com.spoonofcode.poa.feature.profile.ProfileResult

class GetProfileUseCase(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(userId: Int): ProfileResult {
        val user = userRepository.read(id = userId) ?: return ProfileResult.UserNotFound
        val numberOfProductsCreatedByUser = productRepository.countByOwnerUserId(userId)
        return ProfileResult.Success(
            Profile(
                name = getFullName(user = user),
                numberOfProductsOwnedByUser = numberOfProductsCreatedByUser,
            )
        )
    }

    private fun getFullName(user: User): String = buildString {
        append("${user.firstName} ${user.lastName}")
        user.nickName?.let { append(" ($it)") }
    }
}