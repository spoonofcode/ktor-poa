package com.spoonofcode.poa.feature.user

// Importy
import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidParameter
import com.spoonofcode.poa.core.domain.user.GetAllPartnersForUserUseCase // Upewnij się, że ten import jest poprawny
import com.spoonofcode.poa.core.base.routes.CrudOperation
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.UserPartnersRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.userPartners(
    userPartnersRepository: UserPartnersRepository = get(),
    getAllPartnersForUserUseCase: GetAllPartnersForUserUseCase = get()
) {
    val basePath = "/userPartners"

    crudRoute(
        basePath = basePath,
        repository = userPartnersRepository,
        CrudOperation.Read,
        CrudOperation.All,
        CrudOperation.Create,
        CrudOperation.Update,
        CrudOperation.Delete
    )

    route(basePath) {
        get("/{userId}/partners") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull
            ) { userId ->
                call.safeRespond {
                    val partners = getAllPartnersForUserUseCase(userId)
                    call.respond(HttpStatusCode.OK, partners)
                }
            }
        }
    }
}