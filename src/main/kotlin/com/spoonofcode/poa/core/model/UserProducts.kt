package com.spoonofcode.poa.core.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserProducts : Table() {
    val userId = reference(
        name = "user_id",
        refColumn = Products.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val productId = reference("product_id", Users.id)
    override val primaryKey = PrimaryKey(userId, productId)
}