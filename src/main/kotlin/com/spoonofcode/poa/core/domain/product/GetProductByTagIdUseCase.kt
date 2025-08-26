package com.spoonofcode.poa.core.domain.product

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.model.Product

class GetProductByTagIdUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(tagId: String): Product = productRepository.readByTagId(tagId = tagId)
}