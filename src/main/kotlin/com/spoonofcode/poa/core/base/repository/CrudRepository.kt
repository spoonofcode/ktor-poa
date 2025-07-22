package com.spoonofcode.poa.core.base.repository

import com.spoonofcode.poa.plugins.dbQuery
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

interface CrudRepository<RQ, RS> {
    suspend fun create(request: RQ): RS
    suspend fun read(id: Int): RS?
    suspend fun update(id: Int, request: RQ): Boolean
    suspend fun delete(id: Int): Boolean
    suspend fun readAll(): List<RS>
}

abstract class GenericCrudRepository<T : IntIdTable, RQ, RS>(
    private val table: T,
    private val leftJoinTables: List<IntIdTable> = emptyList(),
    private val toResultRow: (RQ) -> Map<Column<out Any?>, Any?>,
    val toResponse: (ResultRow) -> RS
) : CrudRepository<RQ, RS> {

    override suspend fun create(request: RQ): RS = dbQuery {
        val id = table.insertAndGetId { statement ->
            toResultRow(request).forEach { (column, value) ->
                statement[column as Column<Any?>] = value
            }
        }.value

        if (leftJoinTables.isNotEmpty()) {
            createQueryWithJoinLeftTables().selectAll().where { table.id eq id }
                .single().let { toResponse(it) }
        } else {
            table.selectAll().where { table.id eq id }
                .single().let { toResponse(it) }
        }
    }

    override suspend fun read(id: Int): RS? = dbQuery {
        if (leftJoinTables.isNotEmpty()) {
            createQueryWithJoinLeftTables().selectAll().where { table.id eq id }
                .singleOrNull()?.let { toResponse(it) }
        } else {
            table.selectAll().where { table.id eq id }
                .singleOrNull()?.let { toResponse(it) }
        }
    }

    override suspend fun update(id: Int, request: RQ): Boolean = dbQuery {
        table.update({ table.id eq id }) { statement ->
            toResultRow(request).forEach { (column, value) ->
                statement[column as Column<Any?>] = value
            }
        } > 0
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        table.deleteWhere { table.id eq id } > 0
    }

    override suspend fun readAll(): List<RS> = dbQuery {
        if (leftJoinTables.isNotEmpty()) {
            createQueryWithJoinLeftTables().selectAll().map(toResponse)
        } else {
            table.selectAll().map(toResponse)
        }
    }

    // region Private methods
    private fun createQueryWithJoinLeftTables(): Join {
        // Start with the first left join to initialize the query as a Join type
        var query = table.leftJoin(leftJoinTables.first())

        // Loop through the rest of the tables (starting from the second element)
        leftJoinTables.drop(1).forEach { joinTable ->
            query = query.leftJoin(joinTable)
        }
        return query
    }
    // endregion
}