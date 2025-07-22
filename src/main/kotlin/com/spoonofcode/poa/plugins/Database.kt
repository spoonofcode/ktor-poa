package com.spoonofcode.poa.plugins

import com.spoonofcode.poa.core.model.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun Application.configureDatabases() {
    val driverClass = environment.config.property("storage.driverClassName").getString()
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()
    val db = Database.connect(provideDataSource(jdbcUrl, driverClass))
    transaction(db) {
        dropTables()
        SchemaUtils.create(
            Users,
            Roles,
            UserRoles,
            Clubs,
            Levels,
            Rooms,
            Types,
            SportEvents,
            SportEventUsers
        )
        updateSportEventTrigger()
        setExampleData()
    }
}

private fun provideDataSource(url: String, driverClass: String): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = driverClass
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

private fun dropTables() {
    transaction {
        SchemaUtils.drop(
            Users,
            Roles,
            UserRoles,
            Clubs,
            Levels,
            Rooms,
            Types,
            SportEvents,
            SportEventUsers
        ) // Add all the tables you want to drop here
    }
}

private fun setExampleData() {
    Roles.insert { it[name] = "ADMIN" }
    Roles.insert { it[name] = "CLUB_OWNER" }
    Roles.insert { it[name] = "COACH" }
    Roles.insert { it[name] = "USER" }

    Users.insert {
        it[firstName] = "Bartosz"
        it[lastName] = "Łuczak"
        it[nickName] = "Lycha"
        it[email] = "luczak.bartosz5@gmail.com"
        it[provider] = "google"
        it[providerId] = "117628026316806676295"
    }

    UserRoles.insert {
        it[userId] = 1
        it[roleId] = 1
    }

    Users.insert {
        it[firstName] = "Bartosz"
        it[lastName] = "Luczak"
        it[nickName] = "Lycha"
        it[email] = "bartosz.luczak@gmail.com"
        it[password] = "\$2a\$10\$JvONt8faWClBF4Y5D.9uQO8x2DJDDiVw8VcRwBWmB94tP67WQKNtK"
    }

    UserRoles.insert {
        it[userId] = 2
        it[roleId] = 1
    }

    Users.insert {
        it[firstName] = "Michal"
        it[lastName] = "Staroszczyk"
        it[email] = "michal.staroszczyk@gmail.com"
        it[password] = "\$2a\$10\$zCx5qtCaWxzP/L6hB3pH6u0wMhvev3WAqokQQ8UcmnYDOI6bNEjS."
    }

    UserRoles.insert {
        it[userId] = 3
        it[roleId] = 2
    }

    Users.insert {
        it[firstName] = "Artur"
        it[lastName] = "Mackow"
        it[nickName] = "Tatanka"
        it[email] = "artur.mackow@gmail.com"
        it[password] = "\$2a\$10\$0AgsnrhIbbq3e0jWeW.g0.kniIrjjCXWAs81y69hymh.04YJTKmC."
    }

    UserRoles.insert {
        it[userId] = 4
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Dawid"
        it[lastName] = "Platek"
        it[nickName] = "Pajak"
        it[email] = "dawid.platek@gmail.com"
        it[password] = "\$2a\$10\$txXhfXroVdyly1WtQFOw5OfHilnZzKp5ZTptQJcErIk5GErnAhh7e"
    }

    UserRoles.insert {
        it[userId] = 5
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Jacek"
        it[lastName] = "Bajor"
        it[email] = "jacek.bajor@gmail.com"
        it[password] = "\$2a\$10\$Biyt911mKRL06oNv9M97QeEifQA76lvcWJpyppPT.puI5tQWW97tW"
    }

    UserRoles.insert {
        it[userId] = 6
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Kamil"
        it[lastName] = "laszczyk"
        it[email] = "kamil.laszczyk@gmail.com"
        it[password] = "\$2a\$10\$gU0tHDLR4H6bLE9Ra0wfEOJh05.XxoC03c0XRBKK2G4gMU9/Q7tFm"
    }

    UserRoles.insert {
        it[userId] = 7
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Piotr"
        it[lastName] = "Jakubowski"
        it[nickName] = "Piter"
        it[email] = "piotr.jakubowski@gmail.com"
        it[password] = "\$2a\$10\$E0lMFgY72H0KxebWd2RqsuJxKwDM7bNT78TUXl3/gIyxRBPHmAYQG"
    }

    UserRoles.insert {
        it[userId] = 8
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Wiktoria"
        it[lastName] = "Zakrzewska"
        it[nickName] = "Wika"
        it[email] = "wiktoria.zakrzewska@gmail.com"
        it[password] = "\$2a\$10\$BfQpbNqb2B5lrtrhQmvjCux0H6wcp2uGnbhk3x7zVg.cpz3ojER0W"
    }

    UserRoles.insert {
        it[userId] = 9
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Lukasz"
        it[lastName] = "Skrzypnik"
        it[nickName] = "Perun"
        it[email] = "lukasz.skrzypnik@gmail.com"
        it[password] = "\$2a\$10\$A.bhOFmqlC2iTFCg2Eo2VegQflwP03jq884jevIpHuF2V2zWnp6VG"
    }

    UserRoles.insert {
        it[userId] = 10
        it[roleId] = 3
    }

    Users.insert {
        it[firstName] = "Jan"
        it[lastName] = "Kowalski"
        it[nickName] = "User1"
        it[email] = "jan.kowalski@gmail.com"
        it[password] = "\$2a\$10\$RSIgzWxRf75Gbkg4Kul3EOtWREQ4SiDin6N0.D48gx8NEJk63K/hq"
    }

    UserRoles.insert {
        it[userId] = 11
        it[roleId] = 4
    }

    Users.insert {
        it[firstName] = "Adam"
        it[lastName] = "Nowak"
        it[nickName] = "User2"
        it[email] = "adam.nowak@gmail.com"
        it[password] = "\$2a\$10\$mjR7cnFSIob2/8KuxEzyXucqQ5/X330Ie4uVHjTfbP1e8tRcinpF2"
    }

    UserRoles.insert {
        it[userId] = 12
        it[roleId] = 4
    }

    Users.insert {
        it[firstName] = "Katarzyna"
        it[lastName] = "Solska"
        it[nickName] = "User3"
        it[email] = "katarzyna.solska@gmail.com"
        it[password] = "\$2a\$10\$TbCkwakRuJ9/MsEk0O85Cez5qSXwn2CdANVbcvmu2zIsDnWjiNz3q"
    }

    UserRoles.insert {
        it[userId] = 13
        it[roleId] = 4
    }

    Clubs.insert {
        it[name] = "Klub Krav Gym Wroclaw"
        it[location] = "Wroclaw"
    }

    Clubs.insert {
        it[name] = "Klub Siechnice"
        it[location] = "Siechnice"
    }

    Clubs.insert {
        it[name] = "Klub Wysoka"
        it[location] = "Wysoka"
    }

    Levels.insert {
        it[name] = "Beginner"
    }

    Levels.insert {
        it[name] = "Basic"
    }

    Levels.insert {
        it[name] = "Advance"
    }

    Levels.insert {
        it[name] = "Pro"
    }

    Rooms.insert {
        it[name] = "Training room no. 1"
    }

    Rooms.insert {
        it[name] = "Training room no. 2"
    }

    Rooms.insert {
        it[name] = "Training room no. 3"
    }

    Types.insert {
        it[name] = "Boks"
    }

    Types.insert {
        it[name] = "Grappling"
    }

    Types.insert {
        it[name] = "Mlodziez (10-14 lat)"
    }

    Types.insert {
        it[name] = "Grupa poczatkujaca naborowa"
    }

    Types.insert {
        it[name] = "Grupa Poczatkujaca"
    }

    Types.insert {
        it[name] = "Trening indywidualny"
    }

    Types.insert {
        it[name] = "Trening motoryczny"
    }

    Types.insert {
        it[name] = "Trening otwarty dla wszzystkich"
    }

    Types.insert {
        it[name] = "Wolny Klub - Wolna Mata!"
    }

    Types.insert {
        it[name] = "Grupa Zaawansowana"
    }

    seedSportEvents()

    SportEventUsers.insert {
        it[sportEventId] = 1
        it[userId] = 1
    }

    SportEventUsers.insert {
        it[sportEventId] = 2
        it[userId] = 1
    }
}

fun seedSportEvents() {
    val now = Clock.System.now()

    repeat(200) { index ->
        val startInstant = now + (1..30).random().days + (1..24).random().hours
        val durationMin = (30..120).random()
        val endInstant = startInstant + durationMin.minutes

        SportEvents.insert {
            it[title] = "Trening #${index + 1}"
            it[description] = "Przykładowy opis wydarzenia nr ${index + 1}"
            it[minNumberOfPeople] = 4
            it[maxNumberOfPeople] = 18
            it[cost] = (4..12).random() * 25
            it[startDateTime] = startInstant.toLocalDateTime(TimeZone.UTC)
            it[endDateTime] = endInstant.toLocalDateTime(TimeZone.UTC)
            it[clubId] = (1..3).random()
            it[roomId] = (1..3).random()
            it[typeId] = (1..10).random()
            it[levelId] = (1..4).random()
            it[creatorUserId] = (1..12).random()
        }
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }