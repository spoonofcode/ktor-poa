package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.*
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.selectAll

class ProductRepository : GenericCrudRepository<Products, ProductRequest, ProductResponse>(
    table = Products,
    leftJoinTables = listOf(Users),
    toResultRow = { request ->
        mapOf(
            Products.name to request.name,
            Products.description to request.description,
            Products.collectionName to request.collectionName,
            Products.websiteLink to request.websiteLink,
            Products.customLink to request.customLink,
            Products.ownerUserId to EntityID(request.ownerUserId, Users),
        )
    },
    toResponse = { row ->
        ProductResponse(
            id = row[Products.id].value,
            name = row[Products.name],
            description = row[Products.description],
            tagId = row[Products.tagId],
            collectionName = row[Products.collectionName],
            websiteLink = row[Products.websiteLink],
            customLink = row[Products.customLink],
            ownerUser = UserResponse(
                id = row[Users.id].value,
                firstName = row[Users.firstName],
                lastName = row[Users.lastName],
                nickName = row[Users.nickName],
                email = row[Users.email],
            ),
        )
    }
) {
    suspend fun readByOwnerUserId(ownerUserId: Int): List<ProductResponse> {
        return dbQuery {
            Products
                .leftJoin(Users)
                .selectAll().where { Products.ownerUserId eq ownerUserId }.map {
                    toResponse(it)
                }
        }
    }

    suspend fun countByOwnerUserId(userId: Int): Long {
        return dbQuery {
            Products.selectAll().where { Products.ownerUserId eq userId }.count()
        }
    }
}