package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class Role(
    val id: Int,
    val name: String,
)

@Serializable
data class RoleRequest(
    val name: String,
)

object Roles : IntIdTable() {
    val name = varchar("name", 20)
}