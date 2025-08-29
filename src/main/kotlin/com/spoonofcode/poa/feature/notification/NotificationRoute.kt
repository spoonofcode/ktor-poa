package com.spoonofcode.poa.feature.notification

import com.spoonofcode.poa.core.base.routes.CrudOperation
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.NotificationRepository
import com.spoonofcode.poa.core.domain.notification.SendNotificationUseCase
import com.spoonofcode.poa.core.model.NotificationRequest
import com.spoonofcode.poa.core.network.ext.safeRespond
import com.spoonofcode.poa.core.network.ext.withValidBody
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.notifications(
    notificationRepository: NotificationRepository = get(),
    sendNotificationUseCase: SendNotificationUseCase = get(),
) {
    val basePath = "/notifications"

    route(basePath) {
        post("/") {
            call.withValidBody<NotificationRequest> { body ->
                call.safeRespond {
                    val notification = sendNotificationUseCase(body)
                    call.respond(HttpStatusCode.Created, notification)
                }
            }
        }
    }

    crudRoute(
        basePath = basePath,
        repository = notificationRepository,
        CrudOperation.Read, CrudOperation.All
    )
}
