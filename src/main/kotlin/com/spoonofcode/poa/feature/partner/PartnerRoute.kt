package com.spoonofcode.poa.feature.partner

import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.PartnerRepository
import com.spoonofcode.poa.core.domain.partner.GetPartnerUseCase
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.partners(
    partnerRepository: PartnerRepository = get(),
    getPartnerUseCase: GetPartnerUseCase = get()
) {
    val basePath = "/partners"
    crudRoute(
        basePath = basePath,
        repository = partnerRepository
    )


//    route("/partners") {
//        get("/{partnerId}") {
//            call.withValidParameter(
//                paramName = "partnerId",
//                parser = String::toIntOrNull
//            ) { partnerId ->
//                call.safeRespond {
//                    when (val result = getPartnerUseCase(partnerId = partnerId)) {
//                        is PartnerResult.Success -> {
//                            call.respond(HttpStatusCode.OK, result.partner)
//                        }
//
//                        PartnerResult.NotFound -> {
//                            call.respond(HttpStatusCode.NotFound, "Partner with id = $partnerId not found.")
//                        }
//                    }
//                }
//            }
//        }
//    }
}
