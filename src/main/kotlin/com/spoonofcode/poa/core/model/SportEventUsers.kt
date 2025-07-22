package com.spoonofcode.poa.core.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object SportEventUsers : Table() {
    val sportEventId = reference(
        name = "sport_event_id",
        refColumn = SportEvents.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val userId = reference("user_id", Users.id)
    override val primaryKey = PrimaryKey(sportEventId, userId)
}