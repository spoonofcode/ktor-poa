package com.spoonofcode.poa.feature.profile

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidParameter
import com.spoonofcode.poa.core.domain.GetProfileUseCase
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.profile(getProfileUseCase: GetProfileUseCase = get()) {
    route("/profile") {
        get("/{userId}") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull
            ) { userId ->
                call.safeRespond {
                    when (val result = getProfileUseCase(userId = userId)) {
                        is ProfileResult.Success -> {
                            call.respond(HttpStatusCode.OK, result.profile)
                        }

                        ProfileResult.UserNotFound -> {
                            call.respond(HttpStatusCode.NotFound, "User with id = $userId not found.")
                        }
                    }
                }
            }
        }
    }
}
