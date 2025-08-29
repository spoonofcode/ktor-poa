package com.spoonofcode.poa.feature.login.login

import com.spoonofcode.poa.core.domain.login.LoginGoogleUseCase
import com.spoonofcode.poa.core.model.LoginGoogleRequest
import com.spoonofcode.poa.core.network.ext.safeRespond
import com.spoonofcode.poa.core.network.ext.withValidBody
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.loginGoogle(loginGoogleUseCase: LoginGoogleUseCase = get()) {
    route("/login/google") {
        post("/") {
            call.withValidBody<LoginGoogleRequest> { body ->
                call.safeRespond {
                    when (val result = loginGoogleUseCase(googleUserToken = body.googleUserToken)) {
                        is LoginGoogleResult.Success -> {
                            call.respond(HttpStatusCode.OK, result.loginGoogle)
                        }

                        LoginGoogleResult.InvalidCredentials -> {
                            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
                        }
                    }
                }
            }
        }
    }
}
