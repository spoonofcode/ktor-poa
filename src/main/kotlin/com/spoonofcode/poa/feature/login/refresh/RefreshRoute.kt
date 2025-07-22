package com.spoonofcode.poa.feature.login.refresh

import com.spoonofcode.poa.core.network.JwtConfig
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.refresh() {
    route("/refresh") {
        post("/") {
            val refreshToken = call.request.headers["Authorization"]?.removePrefix("Bearer ")
            if (refreshToken.isNullOrBlank()) {
                call.respondText("No refresh token provided", status = HttpStatusCode.BadRequest)
                return@post
            }

            try {
                val verifier = JwtConfig.getVerifier()
                val decodedJWT = verifier.verify(refreshToken)

                // Check if it's actually a refresh token
                val isRefresh = decodedJWT.getClaim(JwtConfig.CLAIM_REFRESH).asBoolean()
                if (!isRefresh) {
                    call.respondText("Not a refresh token", status = HttpStatusCode.BadRequest)
                    return@post
                }

                val userId = decodedJWT.subject
                val email = decodedJWT.getClaim(JwtConfig.CLAIM_EMAIL).asString()

                // Generate new tokens
                val newJwtAccessToken = JwtConfig.createAccessToken(userId = userId, email = email)
                val newJwtRefreshToken = JwtConfig.createRefreshToken(userId = userId, email = email)

                call.respond(
                    mapOf(
                        "jwtAccessToken" to newJwtAccessToken,
                        "jwtRefreshToken" to newJwtRefreshToken
                    )
                )
            } catch (e: Exception) {
                call.respondText("Invalid or expired refresh token", status = HttpStatusCode.Unauthorized)
            }

        }
    }
}