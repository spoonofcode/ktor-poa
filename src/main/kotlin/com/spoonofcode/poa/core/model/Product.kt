package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val seriesId: String,
    val collectionName: String,
    val imageLink: String,
    val tagId: String? = null,
    val websiteLink: String? = null,
    val customLink: String? = null,
    val ownerUser: User? = null,
)

@Serializable
data class ProductRequest(
    val name: String,
    val description: String,
    val seriesId: String,
    val collectionName: String,
    val imageLink: String,
    val tagId: String? = null,
    val websiteLink: String? = null,
    val customLink: String? = null,
    val ownerUserId: Int? = null,
)

object Products : IntIdTable() {
    val name = varchar("name", 128)
    val description = varchar("description", 128)
    val seriesId = varchar("series_id", 128)
    val collectionName = varchar("collection_name", 128)
    val imageLink = varchar("imageLink", 255)
    val tagId = varchar("tag_id", 128).nullable()
    val websiteLink = varchar("website_link", 255).nullable()
    val customLink = varchar("customLink", 255).nullable()
    val ownerUserId = reference("owner_user_id", Users).nullable()
}