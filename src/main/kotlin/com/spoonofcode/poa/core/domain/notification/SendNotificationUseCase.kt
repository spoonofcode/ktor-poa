package com.spoonofcode.poa.core.domain.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import com.spoonofcode.poa.core.data.repository.NotificationRepository
import com.spoonofcode.poa.core.model.NotificationRequest

class SendNotificationUseCase(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(notificationRequest: NotificationRequest): com.spoonofcode.poa.core.model.Notification {
        val notification = notificationRepository.create(notificationRequest)
        val data = mapOf(NOTIFICATION_ID_KEY to notification.id.toString())
        with(notificationRequest) {
            if (seriesIds.isNotEmpty()) {
                seriesIds.forEach { seriesId ->
                    FirebaseMessaging.getInstance().send(buildNotificationForSeries(seriesId, data))
                }
            }
        }
        return notification
    }

    private fun NotificationRequest.buildNotificationForSeries(seriesId: String, data: Map<String, String>): Message? =
        Message.builder()
            .setNotification(
                buildNotification(
                    title = title,
                    text = text,
                    imageUrl = imageUrl
                )
            )
            .setTopic(seriesId)
            .putAllData(data)
            .build()

    private fun buildNotification(
        title: String,
        text: String,
        imageUrl: String?
    ): Notification =
        Notification.builder()
            .setTitle(title)
            .setBody(text)
            .apply {
                imageUrl?.let { setImage(it) }
            }
            .build()

    private companion object {
        private const val NOTIFICATION_ID_KEY = "notificationId"
    }
}