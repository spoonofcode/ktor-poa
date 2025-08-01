package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class UserRequest(
    val firstName: String,
    val lastName: String,
    val nickName: String? = null,
    val email: String,
    val password: String? = null,
    val provider: String? = null,
    val providerId: String? = null,
)

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val nickName: String? = null,
    val email: String,
)

object Users : IntIdTable() {
    val firstName = varchar("first_name", 128)
    val lastName = varchar("last_name", 128)
    val nickName = varchar("nick_name", 128).nullable()
    val email = varchar("email", 255)
    val password = varchar("password", 255).nullable()
    val provider = varchar("provider", 50).nullable()
    val providerId = varchar("provider_id", 255).nullable()
}