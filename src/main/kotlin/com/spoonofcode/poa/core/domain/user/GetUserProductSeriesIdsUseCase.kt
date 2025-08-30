package com.spoonofcode.poa.core.domain.user

import com.spoonofcode.poa.core.data.repository.ProductRepository

class GetUserProductSeriesIdsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(userId: Int): List<String> =
        productRepository.readUserProductSeriesIds(userId = userId)
}