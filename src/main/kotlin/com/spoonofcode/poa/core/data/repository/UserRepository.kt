package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.User
import com.spoonofcode.poa.core.model.UserRequest
import com.spoonofcode.poa.core.model.Users
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.selectAll

class UserRepository : GenericCrudRepository<Users, UserRequest, User>(
    table = Users,
    toResultRow = { request ->
        mapOf(
            Users.firstName to request.firstName,
            Users.lastName to request.lastName,
            Users.nickName to request.nickName,
            Users.email to request.email,
            Users.password to request.password,
            Users.provider to request.provider,
            Users.providerId to request.providerId,
        )
    },
    toResponse = { row ->
        User(
            id = row[Users.id].value,
            firstName = row[Users.firstName],
            lastName = row[Users.lastName],
            nickName = row[Users.nickName],
            email = row[Users.email],
        )
    }
) {
    suspend fun readByEmail(email: String): User? {
        return dbQuery {
            Users.selectAll().where { Users.email eq email }.map { toResponse(it) }
        }.firstOrNull()
    }

    suspend fun readPassword(email: String): String {
        return dbQuery {
            Users.selectAll().where { Users.email eq email }.map { it[Users.password] }.firstOrNull() ?: EMPTY_PASSWORD
        }
    }

    companion object {
        private const val EMPTY_PASSWORD = ""
    }
}

