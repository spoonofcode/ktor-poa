package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.model.SportEventUsers
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.insert

class SportEventUsersRepository {
    suspend fun addUserToSportEvent(userId: Int, sportEventId: Int) {
        dbQuery {
            SportEventUsers.insert {
                it[SportEventUsers.userId] = userId
                it[SportEventUsers.sportEventId] = sportEventId
            }
        }
    }
}