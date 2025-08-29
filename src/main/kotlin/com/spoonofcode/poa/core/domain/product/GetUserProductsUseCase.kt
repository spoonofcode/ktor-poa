package com.spoonofcode.poa.core.domain.product

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.model.Product

class GetUserProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(ownerUserId: Int): List<Product> =
        productRepository.readByOwnerUserId(ownerUserId = ownerUserId)
}