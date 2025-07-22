package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.SportEventResponse

class GetSportEventsUserParticipatedInUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: Int): List<SportEventResponse> {
        return userRepository.readSportEventsInWhichUserParticipates(userId = userId)
    }
}