package com.spoonofcode.poa.routes

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.ClubRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.clubs(clubRepository: ClubRepository = get()) {
    crudRoute(
        basePath = "/clubs",
        repository = clubRepository,
    )
}