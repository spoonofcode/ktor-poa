package com.spoonofcode.poa.feature.notification

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.NotificationRepository
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.notifications(
    notificationRepository: NotificationRepository = get(),
) {
    val basePath = "/notifications"
    crudRoute(
        basePath = basePath,
        repository = notificationRepository
    )
}
