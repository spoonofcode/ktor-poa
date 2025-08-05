package com.spoonofcode.poa.core.data.repository

import com.spoonofcode.poa.core.base.repository.GenericCrudRepository
import com.spoonofcode.poa.core.model.PartnerCategories
import com.spoonofcode.poa.core.model.PartnerCategory
import com.spoonofcode.poa.core.model.PartnerCategoryRequest

class PartnerCategoryRepository :
    GenericCrudRepository<PartnerCategories, PartnerCategoryRequest, PartnerCategory>(
        table = PartnerCategories,
        toResultRow = { request ->
            mapOf(
                PartnerCategories.name to request.name,
            )
        },
        toResponse = { row ->
            PartnerCategory(
                id = row[PartnerCategories.id].value,
                name = row[PartnerCategories.name],
            )
        }
    )