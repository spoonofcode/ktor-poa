package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.LevelRequest
import com.spoonofcode.poa.core.model.LevelResponse
import com.spoonofcode.poa.core.model.Levels

class LevelRepository : GenericCrudRepository<Levels, LevelRequest, LevelResponse>(
    table = Levels,
    toResultRow = { request ->
        mapOf(
            Levels.name to request.name,
        )
    },
    toResponse = { row ->
        LevelResponse(
            id = row[Levels.id].value,
            name = row[Levels.name],
        )
    }
)

