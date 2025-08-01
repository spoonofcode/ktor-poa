package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.model.UserProducts
import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.sql.insert

class UserProductsRepository {
    suspend fun addProductToUser(productId: Int, userId: Int) {
        dbQuery {
            UserProducts.insert {
                it[UserProducts.productId] = productId
                it[UserProducts.userId] = userId
            }
        }
    }
}