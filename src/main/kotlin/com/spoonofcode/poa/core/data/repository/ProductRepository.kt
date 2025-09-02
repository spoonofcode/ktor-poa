package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.*
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ProductRepository : GenericCrudRepository<Products, ProductRequest, Product>(
    table = Products,
    leftJoinTables = listOf(Users),
    toResultRow = { request ->
        mapOf(
            Products.name to request.name,
            Products.description to request.description,
            Products.seriesId to request.seriesId,
            Products.collectionName to request.collectionName,
            Products.imageLink to request.imageLink,
            Products.videoLink to request.videoLink,
            Products.tagId to request.tagId,
            Products.websiteLink to request.websiteLink,
            Products.customLink to request.customLink,
            Products.ownerUserId to if (request.ownerUserId != null) {
                EntityID(request.ownerUserId, Users)
            } else {
                null
            },
        )
    },
    toResponse = { row ->
        Product(
            id = row[Products.id].value,
            name = row[Products.name],
            description = row[Products.description],
            seriesId = row[Products.seriesId],
            collectionName = row[Products.collectionName],
            imageLink = row[Products.imageLink],
            videoLink = row[Products.videoLink],
            tagId = row[Products.tagId],
            websiteLink = row[Products.websiteLink],
            customLink = row[Products.customLink],
            ownerUser = if (row[Products.ownerUserId] != null) {
                User(
                    id = row[Users.id].value,
                    firstName = row[Users.firstName],
                    lastName = row[Users.lastName],
                    nickName = row[Users.nickName],
                    email = row[Users.email],
                )
            } else {
                null
            },
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

    suspend fun readByOwnerUserId(userId: Int): List<Product> {
        return dbQuery {
            Products
                .leftJoin(Users)
                .selectAll().where { Products.ownerUserId eq userId }.map {
                    toResponse(it)
                }
        }
    }

    suspend fun countByOwnerUserId(userId: Int): Long {
        return dbQuery {
            Products
                .leftJoin(Users)
                .selectAll().where { Products.ownerUserId eq userId }.count()
        }
    }

    suspend fun addOwnerUserId(productId: Int, userId: Int) {
        return dbQuery {
            Products.update({ Products.id eq productId }) {
                it[ownerUserId] = userId
            }
        }
    }

    suspend fun readUserProductSeriesIds(userId: Int): List<String> {
        return dbQuery {
            Products
                .select(Products.seriesId)
                .where { Products.ownerUserId eq userId }
                .withDistinct()
                .map { it[Products.seriesId] }
        }
    }
}