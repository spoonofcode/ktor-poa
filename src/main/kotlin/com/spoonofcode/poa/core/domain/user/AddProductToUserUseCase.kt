package com.spoonofcode.poa.core.domain.user

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.feature.product.ProductResult

class AddProductToUserUseCase(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(productId: Int, userId: Int): ProductResult = when {
        userRepository.read(userId) == null -> ProductResult.UserNotFound
        productRepository.read(productId) == null -> ProductResult.ProductNotFound
        else -> {
            try {
                productRepository.addOwnerUserId(productId = productId, userId = userId)
                ProductResult.Success
            } catch (e: Exception) {
                ProductResult.Error
            }
        }
    }
}