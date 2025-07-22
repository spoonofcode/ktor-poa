package com.spoonofcode.poa.core.domain

import com.spoonofcode.poa.core.data.repository.SportEventRepository
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.Profile
import com.spoonofcode.poa.core.model.UserResponse
import com.spoonofcode.poa.feature.profile.ProfileResult

class GetProfileUseCase(
    private val userRepository: UserRepository,
    private val sportEventRepository: SportEventRepository,
) {
    suspend operator fun invoke(userId: Int): ProfileResult {
        val user = userRepository.read(id = userId) ?: return ProfileResult.UserNotFound
        val numberOfEventsCreatedByUser = sportEventRepository.countByCreatorUserId(userId)
        val numberOfEventsUserParticipatedIn = userRepository.countSportEventsInWhichTheUserParticipates(userId)
        return ProfileResult.Success(
            Profile(
                name = getFullName(user = user),
                numberOfEventsCreatedByUser = numberOfEventsCreatedByUser,
                numberOfEventsUserParticipatedIn = numberOfEventsUserParticipatedIn,
            )
        )
    }

    private fun getFullName(user: UserResponse): String = buildString {
        append("${user.firstName} ${user.lastName}")
        user.nickName?.let { append(" ($it)") }
    }
}