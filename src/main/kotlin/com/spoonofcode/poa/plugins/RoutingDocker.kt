package com.spoonofcode.poa.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.routingDocker() {
    routing {
        get("/health") { call.respond(mapOf("status" to "OK")) }
    }
}