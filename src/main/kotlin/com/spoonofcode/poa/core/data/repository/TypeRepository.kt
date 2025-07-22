package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.TypeRequest
import com.spoonofcode.poa.core.model.TypeResponse
import com.spoonofcode.poa.core.model.Types

class TypeRepository : GenericCrudRepository<Types, TypeRequest, TypeResponse>(
    table = Types,
    toResultRow = { request ->
        mapOf(
            Types.name to request.name,
        )
    },
    toResponse = { row ->
        TypeResponse(
            id = row[Types.id].value,
            name = row[Types.name],
        )
    }
)

