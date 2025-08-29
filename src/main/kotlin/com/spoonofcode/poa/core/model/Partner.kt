package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

@Serializable
data class Partner(
    val id: Int,
    val name: String,
    val email: String,
    val description: String,
    val imageLink: String,
    val websiteLink: String? = null,
    val instagramLink: String? = null,
    val facebookLink: String? = null,
    val youtubeLink: String? = null,
    val xLink: String? = null,
)

@Serializable
data class PartnerRequest(
    val name: String,
    val email: String,
    val description: String,
    val imageLink: String,
    val websiteLink: String? = null,
    val instagramLink: String? = null,
    val facebookLink: String? = null,
    val youtubeLink: String? = null,
    val xLink: String? = null,
    val userId: Int,
)

object Partners : IntIdTable() {
    val name = varchar("name", 128)
    val email = varchar("email", 128)
    val description = varchar("description", 255)
    val imageLink = varchar("imageLink", 255)
    val websiteLink = varchar("website_link", 255).nullable()
    val instagramLink = varchar("instagram_link", 255).nullable()
    val facebookLink = varchar("facebook_link", 255).nullable()
    val youtubeLink = varchar("youtube_link", 255).nullable()
    val xLink = varchar("x_link", 255).nullable()

    fun toModel(row: ResultRow) = Partner(
        id = row[id].value,
        name = row[name],
        email = row[email],
        description = row[description],
        imageLink = row[imageLink],
        websiteLink = row[websiteLink],
        instagramLink = row[instagramLink],
        facebookLink = row[facebookLink],
        youtubeLink = row[youtubeLink],
        xLink = row[xLink]
    )
}