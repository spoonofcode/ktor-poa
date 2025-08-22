package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.model.Partner
import com.spoonofcode.poa.core.model.Partners
import com.spoonofcode.poa.core.model.UserPartners
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class UserPartnersRepository {

    suspend fun add(userId: Int, partnerId: Int) {
        dbQuery {
            UserPartners.insert {
                it[user] = userId
                it[partner] = partnerId
            }
        }
    }

    suspend fun remove(userId: Int, partnerId: Int): Int {
        return dbQuery {
            UserPartners.deleteWhere {
                (user eq userId) and (partner eq partnerId)
            }
        }
    }

    suspend fun findAllForUser(userId: Int): List<Partner> {
        return dbQuery {
            (Partners innerJoin UserPartners)
                .selectAll()
                .where { UserPartners.user eq userId }
                .map { Partners.toModel(it) }
        }
    }
}