package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.*
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.selectAll

class SportEventRepository : GenericCrudRepository<SportEvents, SportEventRequest, SportEventResponse>(
    table = SportEvents,
    leftJoinTables = listOf(Clubs, Levels, Rooms, Types, Users),
    toResultRow = { request ->
        mapOf(
            SportEvents.title to request.title,
            SportEvents.description to request.description,
            SportEvents.minNumberOfPeople to request.minNumberOfPeople,
            SportEvents.maxNumberOfPeople to request.maxNumberOfPeople,
            SportEvents.cost to request.cost,
            SportEvents.startDateTime to request.startDateTime,
            SportEvents.endDateTime to request.endDateTime,
            SportEvents.clubId to EntityID(request.clubId, Clubs),
            SportEvents.roomId to EntityID(request.roomId, Rooms),
            SportEvents.typeId to EntityID(request.typeId, Types),
            SportEvents.levelId to EntityID(request.levelId, Levels),
            SportEvents.creatorUserId to EntityID(request.creatorUserId, Users),
        )
    },
    toResponse = { row ->
        SportEventResponse(
            id = row[SportEvents.id].value,
            creationDate = row[SportEvents.creationDate],
            updateDate = row[SportEvents.updateDate],
            title = row[SportEvents.title],
            description = row[SportEvents.description],
            minNumberOfPeople = row[SportEvents.minNumberOfPeople],
            maxNumberOfPeople = row[SportEvents.maxNumberOfPeople],
            cost = row[SportEvents.cost],
            startDateTime = row[SportEvents.startDateTime],
            endDateTime = row[SportEvents.endDateTime],
            club = ClubResponse(row[Clubs.id].value, row[Clubs.name], row[Clubs.location]),
            room = RoomResponse(row[Rooms.id].value, row[Rooms.name]),
            type = TypeResponse(row[Types.id].value, row[Types.name]),
            level = LevelResponse(row[Levels.id].value, row[Levels.name]),
            creatorUser = UserResponse(
                id = row[Users.id].value,
                firstName = row[Users.firstName],
                lastName = row[Users.lastName],
                nickName = row[Users.nickName],
                email = row[Users.email],
            ),
        )
    }
) {
    suspend fun readByCreatorUserId(creatorUserId: Int): List<SportEventResponse> {
        return dbQuery {
            SportEvents
                .leftJoin(Clubs)
                .leftJoin(Levels)
                .leftJoin(Rooms)
                .leftJoin(Types)
                .leftJoin(Users)
                .selectAll().where { SportEvents.creatorUserId eq creatorUserId }.map {
                    toResponse(it)
                }
        }
    }

    suspend fun countByCreatorUserId(userId: Int): Long {
        return dbQuery {
            SportEvents.selectAll().where { SportEvents.creatorUserId eq userId }.count()
        }
    }
}