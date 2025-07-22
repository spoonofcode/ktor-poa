package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class LevelRequest(
    val name: String,
)

@Serializable
data class LevelResponse(
    val id: Int,
    val name: String,
)

object Levels : IntIdTable() {
    val name = varchar("name", 128)
}