package com.spoonofcode.poa.core.domain

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.spoonofcode.poa.core.model.MessageFCM
import com.spoonofcode.poa.core.model.NotificationFCM

class SendMessageFCMUseCase() {
    operator fun invoke(messageFCM: MessageFCM) {
        with(messageFCM) {
            if (!topics.isNullOrEmpty()) {
                topics.forEach { topic ->
                    FirebaseMessaging.getInstance().send(buildMessageForTopic(topic))
                }
            } else {
                FirebaseMessaging.getInstance().sendEachForMulticast(buildMulticastMessage())
            }
        }
    }

    private fun MessageFCM.buildMulticastMessage(): MulticastMessage? = MulticastMessage.builder()
        .setNotification(buildNotification(notification))
        .addAllTokens(tokens)
        .apply {
            data?.let { putAllData(it) }
        }
        .build()

    private fun MessageFCM.buildMessageForTopic(topic: String): Message? = Message.builder()
        .setNotification(buildNotification(notification))
        .setTopic(topic)
        .apply {
            data?.let { putAllData(it) }
        }
        .build()

    private fun buildNotification(notification: NotificationFCM): Notification = Notification.builder()
        .setTitle(notification.title)
        .setBody(notification.body)
        .apply {
            notification.imageUrl?.let { setImage(it) }
        }
        .build()
}