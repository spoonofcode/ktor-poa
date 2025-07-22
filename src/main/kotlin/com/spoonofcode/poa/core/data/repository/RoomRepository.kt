package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.RoomRequest
import com.spoonofcode.poa.core.model.RoomResponse
import com.spoonofcode.poa.core.model.Rooms

class RoomRepository : GenericCrudRepository<Rooms, RoomRequest, RoomResponse>(
    table = Rooms,
    toResultRow = { request ->
        mapOf(
            Rooms.name to request.name,
        )
    },
    toResponse = { row ->
        RoomResponse(
            id = row[Rooms.id].value,
            name = row[Rooms.name],
        )
    }
)

