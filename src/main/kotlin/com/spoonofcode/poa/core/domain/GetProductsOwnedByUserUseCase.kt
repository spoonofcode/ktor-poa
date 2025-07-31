package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.model.ProductResponse

class GetProductsOwnedByUserUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(ownerUserId: Int): List<ProductResponse> = productRepository.readByOwnerUserId(ownerUserId = ownerUserId)
}