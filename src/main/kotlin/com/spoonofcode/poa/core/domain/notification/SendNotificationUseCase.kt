package com.spoonofcode.poa.core.domain.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.spoonofcode.poa.core.data.repository.NotificationRepository
import com.spoonofcode.poa.core.model.NotificationRequest

class SendNotificationUseCase(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(notificationRequest: NotificationRequest) {
        notificationRepository.create(notificationRequest)
        with(notificationRequest) {
            if (seriesIds.isNotEmpty()) {
                seriesIds.forEach { seriesId ->
                    FirebaseMessaging.getInstance().send(buildNotificationForSeries(seriesId))
                }
            }
        }
    }

    private fun NotificationRequest.buildNotificationForSeries(seriesId: String): Message? = Message.builder()
        .setNotification(
            buildNotification(
                title = title,
                text = text,
                link = link,
                imageUrl = imageUrl
            )
        )
        .setTopic(seriesId)
//        .apply {
//            data?.let { putAllData(it) }
//        }
        .build()

    private fun buildNotification(
        title: String,
        text: String,
        link: String?,
        imageUrl: String?
    ): Notification =
        Notification.builder()
            .setTitle(title)
            .setBody(text)
            .apply {
                imageUrl?.let { setImage(it) }
            }
            .build()
}