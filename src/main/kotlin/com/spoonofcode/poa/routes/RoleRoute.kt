package com.spoonofcode.poa.routes

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidQueryParameter
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.RoleRepository
import com.spoonofcode.poa.core.domain.GetAllRolesByUserIdUseCase
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.roles(
    roleRepository: RoleRepository = get(),
    getAllRolesByUserIdUseCase: GetAllRolesByUserIdUseCase = get(),
) {
    val basePath = "/roles"
    crudRoute(
        basePath = basePath,
        repository = roleRepository
    )
    route(basePath) {
        get("") {
            call.withValidQueryParameter<Int>(
                paramName = "userId",
            ) { userId ->
                call.safeRespond {
                    val sportEventsByCreatorRoleId = getAllRolesByUserIdUseCase(userId = userId)
                    call.respond(HttpStatusCode.OK, sportEventsByCreatorRoleId)
                }
            }
        }
    }
}