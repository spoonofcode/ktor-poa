package com.spoonofcode.poa.core.model

import org.jetbrains.exposed.sql.Table

object UserPartners : Table() {
    val user = reference("user_id", Users)
    val partner = reference("partner_id", Partners)
    override val primaryKey = PrimaryKey(user, partner)
}