package com.spoonofcode.poa.core.domain.di

import com.spoonofcode.poa.core.domain.login.LoginGoogleUseCase
import com.spoonofcode.poa.core.domain.login.LoginUseCase
import com.spoonofcode.poa.core.domain.login.RegisterUseCase
import com.spoonofcode.poa.core.domain.notification.SendNotificationUseCase
import com.spoonofcode.poa.core.domain.product.GetProductByTagIdUseCase
import com.spoonofcode.poa.core.domain.profile.GetProfileUseCase
import com.spoonofcode.poa.core.domain.user.AddProductToUserUseCase
import com.spoonofcode.poa.core.domain.user.GetUserProductSeriesIdsUseCase
import com.spoonofcode.poa.core.domain.user.GetUserProductsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::LoginUseCase)
    factoryOf(::LoginGoogleUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::GetProductByTagIdUseCase)
    factoryOf(::GetUserProductsUseCase)
    factoryOf(::AddProductToUserUseCase)
    factoryOf(::SendNotificationUseCase)
    factoryOf(::GetUserProductSeriesIdsUseCase)
}