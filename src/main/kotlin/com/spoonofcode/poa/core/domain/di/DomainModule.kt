package com.spoonofcode.poa.core.domain.di

import com.spoonofcode.poa.core.domain.login.LoginGoogleUseCase
import com.spoonofcode.poa.core.domain.login.LoginUseCase
import com.spoonofcode.poa.core.domain.login.RegisterUseCase
import com.spoonofcode.poa.core.domain.messagefcm.SendMessageFCMUseCase
import com.spoonofcode.poa.core.domain.partner.GetPartnerUseCase
import com.spoonofcode.poa.core.domain.product.AddProductToUserUseCase
import com.spoonofcode.poa.core.domain.product.GetProductsOwnedByUserUseCase
import com.spoonofcode.poa.core.domain.profile.GetProfileUseCase
import com.spoonofcode.poa.core.domain.role.AddRoleToUserUseCase
import com.spoonofcode.poa.core.domain.role.GetAllRolesByUserIdUseCase
import com.spoonofcode.poa.core.domain.user.GetAllUsersByRoleIdUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::LoginUseCase)
    factoryOf(::LoginGoogleUseCase)
    factoryOf(::GetPartnerUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::GetAllRolesByUserIdUseCase)
    factoryOf(::GetAllUsersByRoleIdUseCase)
    factoryOf(::GetProductsOwnedByUserUseCase)
    factoryOf(::AddRoleToUserUseCase)
    factoryOf(::AddProductToUserUseCase)
    factoryOf(::SendMessageFCMUseCase)
}