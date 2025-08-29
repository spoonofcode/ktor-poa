package com.spoonofcode.poa.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

@Serializable
data class NotificationRequest(
    val title: String,
    val text: String,
    val link: String? = null,
    val imageUrl: String? = null,
    val seriesIds: List<String>,
    val expirationDateTime: LocalDateTime? = null,
)

@Serializable
data class Notification(
    val id: Int,
    val title: String,
    val text: String,
    val link: String? = null,
    val expirationDateTime: LocalDateTime? = null,
)

object Notifications : IntIdTable() {
    val title = varchar("title", 128)
    val text = varchar("text", 255)
    val link = varchar("link", 255).nullable()
    val expirationDate = datetime("expiration_date_time").nullable()
}