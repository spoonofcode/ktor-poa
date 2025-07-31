package com.spoonofcode.poa.core.domain.di

import com.spoonofcode.poa.core.domain.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::LoginUseCase)
    factoryOf(::LoginGoogleUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::GetAllRolesByUserIdUseCase)
    factoryOf(::GetAllUsersByRoleIdUseCase)
    factoryOf(::GetProductsOwnedByUserUseCase)
    factoryOf(::AddRoleToUserUseCase)
    factoryOf(::AddProductToUserUseCase)
    factoryOf(::SendMessageFCMUseCase)
}