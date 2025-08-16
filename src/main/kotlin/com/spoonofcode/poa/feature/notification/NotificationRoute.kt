package com.spoonofcode.poa.feature.notification

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidBody
import com.spoonofcode.poa.core.domain.notification.SendNotificationUseCase
import com.spoonofcode.poa.core.model.NotificationRequest
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.notifications(
    sendNotificationUseCase: SendNotificationUseCase = get(),
) {
    route("/notifications") {
        post("/send") {
            call.withValidBody<NotificationRequest> { body ->
                call.safeRespond {
                    sendNotificationUseCase(body)
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}
