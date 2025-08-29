package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.Notification
import com.spoonofcode.poa.core.model.NotificationRequest
import com.spoonofcode.poa.core.model.Notifications

class NotificationRepository : GenericCrudRepository<Notifications, NotificationRequest, Notification>(
    table = Notifications,
    toResultRow = { request ->
        mapOf(
            Notifications.title to request.title,
            Notifications.text to request.text,
            Notifications.link to request.link,
            Notifications.expirationDate to request.expirationDateTime,
        )
    },
    toResponse = { row ->
        Notification(
            id = row[Notifications.id].value,
            title = row[Notifications.title],
            text = row[Notifications.text],
            link = row[Notifications.link],
            expirationDateTime = row[Notifications.expirationDate],
        )
    }
)