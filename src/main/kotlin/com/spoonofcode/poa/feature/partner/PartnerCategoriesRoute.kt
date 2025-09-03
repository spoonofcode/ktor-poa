package com.spoonofcode.poa.feature.partner

import com.spoonofcode.poa.core.base.routes.CrudOperation
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.PartnerCategoryRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.partnerCategories(
    partnerCategoryRepository: PartnerCategoryRepository = get(),
) {
    val basePath = "/partnerCategories"
    crudRoute(
        basePath = basePath,
        repository = partnerCategoryRepository,
        CrudOperation.All,
    )
}
