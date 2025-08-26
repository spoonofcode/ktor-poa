package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.*
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.selectAll

class ProductRepository : GenericCrudRepository<Products, ProductRequest, Product>(
    table = Products,
    leftJoinTables = listOf(Users),
    toResultRow = { request ->
        mapOf(
            Products.name to request.name,
            Products.description to request.description,
            Products.tagId to request.tagId,
            Products.seriesId to request.seriesId,
            Products.collectionName to request.collectionName,
            Products.websiteLink to request.websiteLink,
            Products.customLink to request.customLink,
            Products.ownerUserId to EntityID(request.ownerUserId, Users),
        )
    },
    toResponse = { row ->
        Product(
            id = row[Products.id].value,
            name = row[Products.name],
            description = row[Products.description],
            tagId = row[Products.tagId],
            seriesId = row[Products.seriesId],
            collectionName = row[Products.collectionName],
            imageLink = row[Products.imageLink],
            websiteLink = row[Products.websiteLink],
            customLink = row[Products.customLink],
            ownerUser = User(
                id = row[Users.id].value,
                firstName = row[Users.firstName],
                lastName = row[Users.lastName],
                nickName = row[Users.nickName],
                email = row[Users.email],
            ),
        )
    }
) {

    suspend fun readByTagId(tagId: String): Product {
        return dbQuery {
            Products
                .leftJoin(Users)
                .selectAll().where { Products.tagId eq tagId }.map {
                    toResponse(it)
                }.first()
        }
    }

    suspend fun readByOwnerUserId(ownerUserId: Int): List<Product> {
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