package com.spoonofcode.poa.core.domain.product

import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.model.Product

class GetProductByTagIdUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(tagId: String): Product {
        println("BARTEK testBartek1")
        val testBartek = productRepository.readByTagId(tagId = tagId)
        println("BARTEK testBartek2 = $testBartek")
        return productRepository.readByTagId(tagId = tagId)
    }
}