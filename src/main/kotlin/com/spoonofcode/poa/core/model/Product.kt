package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class ProductRequest(
    val name: String,
    val description: String,
    val collectionName: String,
    val websiteLink: String? = null,
    val customLink: String? = null,
    val ownerUserId: Int,
)

@Serializable
data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val tagId: String,
    val collectionName: String,
    val websiteLink: String? = null,
    val customLink: String? = null,
    val ownerUser: UserResponse,
)

object Products : IntIdTable() {
    val name = varchar("name", 128)
    val description = varchar("description", 128)
    val tagId = varchar("tagId", 128)
    val collectionName = varchar("collectionName", 128)
    val websiteLink = varchar("websiteLink", 255).nullable()
    val customLink = varchar("customLink", 255).nullable()
    val ownerUserId = reference("owner_user_id", Users)
}