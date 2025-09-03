package com.spoonofcode.poa.feature.user

import com.spoonofcode.poa.core.base.routes.CrudOperation
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.domain.user.AddProductToUserUseCase
import com.spoonofcode.poa.core.domain.user.GetUserProductSeriesIdsUseCase
import com.spoonofcode.poa.core.domain.user.GetUserProductsUseCase
import com.spoonofcode.poa.core.model.AddProductToUserRequest
import com.spoonofcode.poa.core.network.ext.safeRespond
import com.spoonofcode.poa.core.network.ext.withValidBody
import com.spoonofcode.poa.core.network.ext.withValidParameter
import com.spoonofcode.poa.feature.product.ProductResult
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.users(
    userRepository: UserRepository = get(),
    getUserProductsUseCase: GetUserProductsUseCase = get(),
    addProductToUserUseCase: AddProductToUserUseCase = get(),
    getUserProductSeriesIdsUseCase: GetUserProductSeriesIdsUseCase = get(),
) {
    val basePath = "/users"
    crudRoute(
        basePath = basePath,
        repository = userRepository,
        CrudOperation.Read, CrudOperation.Update,
    )
    route(basePath) {
        get("/{userId}/products") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull,
            ) { userId ->
                call.safeRespond {
                    val userProducts = getUserProductsUseCase(ownerUserId = userId)
                    call.respond(HttpStatusCode.OK, userProducts)
                }
            }
        }

        post("/{userId}/products") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull
            ) { userId ->
                call.withValidBody<AddProductToUserRequest> { body ->
                    val productId = body.productId
                    call.safeRespond {
                        when (addProductToUserUseCase(productId = productId, userId = userId)) {
                            is ProductResult.Success -> {
                                call.respond(
                                    HttpStatusCode.Created,
                                    "Product with id = $productId added to user with id = $userId."
                                )
                            }

                            is ProductResult.Error -> call.respond(
                                HttpStatusCode.InternalServerError,
                                "Product with id = $productId couldn't be added to user with id = $userId."
                            )

                            is ProductResult.ProductNotFound -> call.respond(
                                HttpStatusCode.NotFound,
                                "Product with id = $productId not found"
                            )

                            is ProductResult.UserNotFound -> call.respond(
                                HttpStatusCode.NotFound,
                                "User with id = $userId not found"
                            )
                        }
                    }
                }
            }
        }

        get("/{userId}/product-series-ids") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull,
            ) { userId ->
                call.safeRespond {
                    val userProductSeriesIds = getUserProductSeriesIdsUseCase(userId = userId)
                    call.respond(HttpStatusCode.OK, userProductSeriesIds)
                }
            }
        }
    }
}