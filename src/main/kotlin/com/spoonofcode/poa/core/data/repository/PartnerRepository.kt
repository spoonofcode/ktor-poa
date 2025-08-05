package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.Partner
import com.spoonofcode.poa.core.model.PartnerRequest
import com.spoonofcode.poa.core.model.Partners

class PartnerRepository : GenericCrudRepository<Partners, PartnerRequest, Partner>(
    table = Partners,
    toResultRow = { request ->
        mapOf(
            Partners.name to request.name,
            Partners.email to request.email,
            Partners.description to request.description,
            Partners.imageLink to request.imageLink,
            Partners.websiteLink to request.websiteLink,
            Partners.instagramLink to request.instagramLink,
            Partners.facebookLink to request.facebookLink,
            Partners.youtubeLink to request.youtubeLink,
            Partners.xLink to request.xLink,
        )
    },
    toResponse = { row ->
        Partner(
            id = row[Partners.id].value,
            name = row[Partners.name],
            email = row[Partners.email],
            description = row[Partners.description],
            imageLink = row[Partners.imageLink],
            websiteLink = row[Partners.websiteLink],
            instagramLink = row[Partners.instagramLink],
            facebookLink = row[Partners.facebookLink],
            youtubeLink = row[Partners.youtubeLink],
            xLink = row[Partners.xLink],
        )
    }
)