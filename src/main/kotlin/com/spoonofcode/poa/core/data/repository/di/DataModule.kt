package com.spoonofcode.poa.core.data.repository.di

import com.spoonofcode.poa.core.data.repository.*
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::ClubRepository)
    singleOf(::LevelRepository)
    singleOf(::RoomRepository)
    singleOf(::SportEventRepository)
    singleOf(::SportEventUsersRepository)
    singleOf(::UserRolesRepository)
    singleOf(::TypeRepository)
    singleOf(::RoleRepository)
    singleOf(::UserRepository)
}