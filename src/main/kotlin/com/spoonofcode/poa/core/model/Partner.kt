package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class PartnerRequest(
    val name: String,
    val description: String,
    val websiteLink: String? = null,
    val instagramLink: String? = null,
    val facebookLink: String? = null,
    val youtubeLink: String? = null,
    val xLink: String? = null,
    val userId: Int,
)

@Serializable
data class Partner(
    val id: Int,
    val name: String,
    val description: String,
    val websiteLink: String? = null,
    val instagramLink: String? = null,
    val facebookLink: String? = null,
    val youtubeLink: String? = null,
    val xLink: String? = null,
)

object Partners : IntIdTable() {
    val name = varchar("name", 128)
    val description = varchar("description", 128)
    val websiteLink = varchar("website_link", 255).nullable()
    val instagramLink = varchar("instagram_link", 255).nullable()
    val facebookLink = varchar("facebook_link", 255).nullable()
    val youtubeLink = varchar("youtube_link", 255).nullable()
    val xLink = varchar("x_link", 255).nullable()
}