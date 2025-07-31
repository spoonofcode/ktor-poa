package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.UserProductsRepository

class AddProductToUserUseCase(
    private val userProductsRepository: UserProductsRepository,
) {
    suspend operator fun invoke(productId: Int, userId: Int) {
        userProductsRepository.addProductToUser(productId = productId, userId = userId)
    }
}