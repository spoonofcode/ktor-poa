package com.spoonofcode.poa.feature.partner

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.PartnerRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.partners(
    partnerRepository: PartnerRepository = get(),
) {
    val basePath = "/partners"
    crudRoute(
        basePath = basePath,
        repository = partnerRepository
    )
}
