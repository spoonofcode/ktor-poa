package com.spoonofcode.poa.core.domain.partner

import com.spoonofcode.poa.core.data.repository.PartnerRepository

class GetPartnerUseCase(
    private val partnerRepository: PartnerRepository,
) {
    suspend operator fun invoke(partnerId: Int): PartnerResult {
        val partner = partnerRepository.read(id = partnerId) ?: return PartnerResult.NotFound
        return PartnerResult.Success(partner = partner)
    }
}