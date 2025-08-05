package com.spoonofcode.poa.core.domain.partner

import com.spoonofcode.poa.core.model.Partner

sealed class PartnerResult {
    data class Success(val partner: Partner) : PartnerResult()
    object NotFound : PartnerResult()
}