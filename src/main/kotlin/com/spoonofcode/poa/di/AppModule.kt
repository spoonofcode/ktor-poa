package com.spoonofcode.poa.di

import com.spoonofcode.poa.core.base.utils.PasswordUtil
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::PasswordUtil)
}