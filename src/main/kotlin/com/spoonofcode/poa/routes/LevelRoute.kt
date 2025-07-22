package com.spoonofcode.poa.routes

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.LevelRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.levels(levelRepository: LevelRepository = get()) {
    crudRoute(
        basePath = "/levels",
        repository = levelRepository,
    )
}