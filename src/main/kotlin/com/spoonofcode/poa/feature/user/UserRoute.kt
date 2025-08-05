package com.spoonofcode.poa.feature.user

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidBody
import com.spoonofcode.poa.core.base.ext.withValidParameter
import com.spoonofcode.poa.core.base.ext.withValidQueryParameter
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.domain.role.AddRoleToUserUseCase
import com.spoonofcode.poa.core.domain.user.GetAllUsersByRoleIdUseCase
import com.spoonofcode.poa.core.model.AddRoleToUserRequest
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.users(
    userRepository: UserRepository = get(),
    getAllUsersByRoleIdUseCase: GetAllUsersByRoleIdUseCase = get(),
    addRoleToUserUseCase: AddRoleToUserUseCase = get(),
) {
    val basePath = "/users"
    crudRoute(
        basePath = basePath,
        repository = userRepository
    )
    route(basePath) {
        get("") {
            call.withValidQueryParameter<Int>(
                paramName = "roleId",
            ) { roleId ->
                call.safeRespond {
                    val usersByRoleId = getAllUsersByRoleIdUseCase(roleId = roleId)
                    call.respond(HttpStatusCode.OK, usersByRoleId)
                }
            }
        }

        post("/{userId}/roles") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull
            ) { userId ->
                call.withValidBody<AddRoleToUserRequest> { body ->
                    call.safeRespond {
                        val roleId = body.roleId
                        addRoleToUserUseCase(roleId, userId)
                        call.respond(
                            HttpStatusCode.Created,
                            "Role with id = $roleId added to user with id = $userId."
                        )
                    }
                }
            }
        }
    }
}