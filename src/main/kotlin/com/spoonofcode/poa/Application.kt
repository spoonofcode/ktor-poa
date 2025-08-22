package com.spoonofcode.poa

import com.spoonofcode.poa.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
   configureMonitoring()
   configureSerialization()
   configureDI()
   configureDatabases()
   //configureOAuthGoogleWebClient()
   configureAuthenticationJWT()
   configureRouting()
   configureFirebase()
}
