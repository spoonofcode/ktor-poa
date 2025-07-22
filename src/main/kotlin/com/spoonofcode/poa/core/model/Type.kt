package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class TypeRequest(
    val name: String,
)

@Serializable
data class TypeResponse(
    val id: Int,
    val name: String,
)

object Types : IntIdTable() {
    val name = varchar("name", 128)
}