package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class RoleRequest(
    val name: String,
)

@Serializable
data class RoleResponse(
    val id: Int,
    val name: String,
)

object Roles : IntIdTable() {
    val name = varchar("name", 20)
}