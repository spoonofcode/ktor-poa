package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.model.SportEventUsers
import com.spoonofcode.poa.core.model.UserRoles
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.insert

class UserRolesRepository {
    suspend fun addRoleToUser(roleId: Int, userId: Int) {
        dbQuery {
            UserRoles.insert {
                it[UserRoles.roleId] = roleId
                it[SportEventUsers.userId] = userId
            }
        }
    }
}