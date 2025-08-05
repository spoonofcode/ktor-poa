package com.spoonofcode.poa.core.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class PartnerCategoryRequest(
    val name: String,
)

@Serializable
data class PartnerCategory(
    val id: Int,
    val name: String,
)

object PartnerCategories : IntIdTable() {
    val name = varchar("name", 128)
}