package com.spoonofcode.poa.core.model

import com.spoonofcode.poa.core.model.Notifications.reference
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

@Serializable
data class UserPartner(
    val id: Int,
    val userId: Int,
    val partnerId: Int
)

@Serializable
data class UserPartnerRequest(
    val userId: Int,
    val partnerId: Int
)

object UserPartners : IntIdTable() {
    val user = reference("user_id", Users, onDelete = ReferenceOption.CASCADE)
    val partner = reference("partner_id", Partners, onDelete = ReferenceOption.CASCADE)
}