package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.SportEventRepository
import com.spoonofcode.poa.core.model.SportEventResponse

class GetSportEventsCreatedByUserUseCase(
    private val sportEventRepository: SportEventRepository,
) {
    suspend operator fun invoke(creatorUserId: Int): List<SportEventResponse> = sportEventRepository.readByCreatorUserId(creatorUserId = creatorUserId)
}