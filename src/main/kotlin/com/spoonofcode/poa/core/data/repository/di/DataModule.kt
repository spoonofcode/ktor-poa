package com.spoonofcode.poa.core.data.repository.di

import com.spoonofcode.poa.core.data.repository.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::UserRolesRepository)
    singleOf(::PartnerCategoryRepository)
    singleOf(::PartnerRepository)
    singleOf(::ProductRepository)
    singleOf(::RoleRepository)
    singleOf(::UserRepository)
    singleOf(::UserProductsRepository)
    singleOf(::UserPartnersRepository)
}