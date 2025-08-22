package com.spoonofcode.poa.feature.user

import com.spoonofcode.poa.core.data.repository.UserPartnersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userPartners() {
    val userPartnersRepository by inject<UserPartnersRepository>()

    route("/users/{userId}/partners") {
        get {
            val userId = call.parameters["userId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")

            val partners = userPartnersRepository.findAllForUser(userId)
            call.respond(partners)
        }

        post("/{partnerId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")
            val partnerId = call.parameters["partnerId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid or missing partnerId")

            userPartnersRepository.add(userId, partnerId)
            call.respond(HttpStatusCode.Created)
        }

        delete("/{partnerId}") {
            val userId = call.parameters["userId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId")
            val partnerId = call.parameters["partnerId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing partnerId")

            val removedCount = userPartnersRepository.remove(userId, partnerId)
            if (removedCount > 0) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}