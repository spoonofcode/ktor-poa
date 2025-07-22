package com.spoonofcode.poa.routes

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.RoomRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.rooms(roomRepository: RoomRepository = get()) {
    crudRoute(
        basePath = "/rooms",
        repository = roomRepository,
    )
}