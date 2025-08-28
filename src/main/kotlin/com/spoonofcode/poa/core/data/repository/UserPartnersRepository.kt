// Zaktualizowany plik UserPartnersRepository.kt
package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.*
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.selectAll

class UserPartnersRepository : GenericCrudRepository<UserPartners, UserPartnerRequest, UserPartner>(
    table = UserPartners,
    toResultRow = { request ->
        mapOf(
            UserPartners.user to request.userId,
            UserPartners.partner to request.partnerId,
        )
    },
    toResponse = { row ->
        UserPartner(
            id = row[UserPartners.id].value,
            userId = row[UserPartners.user].value,
            partnerId = row[UserPartners.partner].value,
        )
    }
) {
    suspend fun findAllForUser(userId: Int): List<Partner> {
        return dbQuery {
            (Partners innerJoin UserPartners)
                .selectAll()
                .where { UserPartners.user eq userId }
                .map { Partners.toModel(it) }
        }
    }
}