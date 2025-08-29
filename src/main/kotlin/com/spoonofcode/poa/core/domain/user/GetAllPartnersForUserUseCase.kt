package com.spoonofcode.poa.core.domain.user

import com.spoonofcode.poa.core.data.repository.UserPartnersRepository
import com.spoonofcode.poa.core.model.Partner

class GetAllPartnersForUserUseCase(
    private val repository: UserPartnersRepository
) {
    suspend operator fun invoke(userId: Int): List<Partner> {
        return repository.findAllForUser(userId)
    }
}