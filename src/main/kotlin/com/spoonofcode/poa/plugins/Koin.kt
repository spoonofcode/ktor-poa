package com.spoonofcode.poa.plugins

import com.spoonofcode.poa.core.data.repository.di.dataModule
import com.spoonofcode.poa.core.domain.di.domainModule
import com.spoonofcode.poa.di.appModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    install(Koin) {
        modules(
            appModule,
            dataModule,
            domainModule,
        )
    }
}