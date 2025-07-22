package com.spoonofcode.poa.feature.messagefcm

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidBody
import com.spoonofcode.poa.core.domain.SendMessageFCMUseCase
import com.spoonofcode.poa.core.model.MessageFCM
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.messageFCM(sendMessageFCMUseCase: SendMessageFCMUseCase = get()) {
    route("/messageFCM") {
        post("/send") {
            call.withValidBody<MessageFCM> { body ->
                call.safeRespond {
                    sendMessageFCMUseCase(body)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}