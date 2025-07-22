package com.spoonofcode.poa.core.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object UserRoles : Table() {
    val userId = reference(
        "user_id",
        Users.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val roleId = reference(
        "role_id",
        Roles.id,
        onDelete = ReferenceOption.CASCADE
    )
}