package com.spoonofcode.poa.core.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class SportEventRequest(
    val title: String,
    val description: String,
    val minNumberOfPeople: Int,
    val maxNumberOfPeople: Int,
    val cost: Int,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val clubId: Int,
    val roomId: Int,
    val typeId: Int,
    val levelId: Int,
    val creatorUserId: Int,
)
@Serializable
data class SportEventResponse(
    val id: Int,
    val creationDate: LocalDateTime,
    val updateDate: LocalDateTime,
    val title: String,
    val description: String,
    val minNumberOfPeople: Int,
    val maxNumberOfPeople: Int,
    val cost: Int,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val club: ClubResponse,
    val room: RoomResponse,
    val type: TypeResponse,
    val level: LevelResponse,
    val creatorUser: UserResponse,
)

object SportEvents : IntIdTable() {
    val title = varchar("title", 128)
    val description = varchar("description", 128)
    val creationDate = datetime("creation_date").defaultExpression(CurrentDateTime)
    val updateDate = datetime("update_date").defaultExpression(CurrentDateTime)
    val minNumberOfPeople = integer("min_number_of_people")
    val maxNumberOfPeople = integer("max_number_of_people")
    val cost = integer("cost")
    val startDateTime = datetime("start_date_time").defaultExpression(CurrentDateTime)
    val endDateTime = datetime("end_date_time").defaultExpression(CurrentDateTime)
    val clubId = reference("club_id", Clubs)
    val roomId = reference("room_id", Rooms)
    val typeId = reference("type_id", Types)
    val levelId = reference("level_id", Levels)
    val creatorUserId = reference("creation_user_id", Users)
}

// We need trigger to update updateDate value on each row update
// In Exposed we don't have now any function like updateExpression
// https://github.com/JetBrains/Exposed/issues/89
//fun updateSportEventTrigger() {
//    val sql = """
//        CREATE TRIGGER update_date_trigger
//        BEFORE UPDATE ON ${SportEvents.tableName}
//        FOR EACH ROW
//        SET NEW.update_date = CURRENT_TIMESTAMP(6);
//    """.trimIndent()
//
//    transaction {
//        exec(sql)
//    }
//}

fun updateSportEventTrigger() {
    val sql = """
        CREATE TRIGGER update_date_trigger
        BEFORE UPDATE ON ${SportEvents.tableName}
        FOR EACH ROW
        SET NEW.update_date = CURRENT_TIMESTAMP(6);
    """.trimIndent()

    try {
        transaction {
            exec(sql)
        }
    } catch (e: ExposedSQLException) {
        if (e.message?.contains("Trigger already exists") == true) {
            println("Trigger 'update_date_trigger' already exists.")
        } else {
            throw e // Rethrow the exception if it's not the expected "trigger exists" error
        }
    }
}

// Custom expression
// val updateDate = datetime("update_date").defaultExpression(CustomLocalDateTime)
// Now we received:
// ERROR Exposed - MySQL 8.3 doesn't support expression 'CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)'
// as default value. Column will be created with NULL marker.
//
//
//object CustomLocalDateTime : Expression<LocalDateTime>() {
//    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
//        append("CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)")
//    }
//}