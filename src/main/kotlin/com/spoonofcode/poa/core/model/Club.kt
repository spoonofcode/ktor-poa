package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class ClubRequest(
    val name: String,
    val location: String,
)

@Serializable
data class ClubResponse(
    val id: Int,
    val name: String,
    val location: String,
)

object Clubs : IntIdTable() {
    val name = varchar("name", 128)
    val location = varchar("location", 128)
}