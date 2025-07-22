package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.ClubRequest
import com.spoonofcode.poa.core.model.ClubResponse
import com.spoonofcode.poa.core.model.Clubs

class ClubRepository : GenericCrudRepository<Clubs, ClubRequest, ClubResponse>(
    table = Clubs,
    toResultRow = { request ->
        mapOf(
            Clubs.name to request.name,
            Clubs.location to request.location,
        )
    },
    toResponse = { row ->
        ClubResponse(
            id = row[Clubs.id].value,
            name = row[Clubs.name],
            location = row[Clubs.location]
        )
    }
)

