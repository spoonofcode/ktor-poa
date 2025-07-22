package com.spoonofcode.poa.routes

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.TypeRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.types(typeRepository: TypeRepository = get()) {
    crudRoute(
        basePath = "/types",
        repository = typeRepository,
    )
}