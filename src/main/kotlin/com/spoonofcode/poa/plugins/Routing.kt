package com.spoonofcode.poa.plugins

import com.spoonofcode.poa.feature.login.login.login
import com.spoonofcode.poa.feature.login.login.loginGoogle
import com.spoonofcode.poa.feature.login.refresh.refresh
import com.spoonofcode.poa.feature.login.register.register
import com.spoonofcode.poa.feature.messagefcm.messageFCM
import com.spoonofcode.poa.feature.partner.partnerCategories
import com.spoonofcode.poa.feature.partner.partners
import com.spoonofcode.poa.feature.product.products
import com.spoonofcode.poa.feature.profile.profile
import com.spoonofcode.poa.feature.user.users
import com.spoonofcode.poa.routes.roles
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        loginGoogle()
        login()
        register()
        refresh()
        messageFCM()
        authenticate("auth-jwt") {
            users()
            roles()
            partners()
            partnerCategories()
            products()
            profile()
            // Static plugin. Try to access `/static/index.html`
            static("/static") {
                resources("static")
            }
        }
    }
}
