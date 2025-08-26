package com.spoonofcode.poa.feature.product

import com.spoonofcode.poa.core.base.ext.safeRespond
import com.spoonofcode.poa.core.base.ext.withValidBody
import com.spoonofcode.poa.core.base.ext.withValidParameter
import com.spoonofcode.poa.core.base.ext.withValidQueryParameter
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.domain.product.AddProductToUserUseCase
import com.spoonofcode.poa.core.domain.product.GetProductByTagIdUseCase
import com.spoonofcode.poa.core.domain.product.GetProductsOwnedByUserUseCase
import com.spoonofcode.poa.core.model.AddProductToUserRequest
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.products(
    getProductByTagIdUseCase: GetProductByTagIdUseCase = get(),
    getProductsOwnedByUserUseCase: GetProductsOwnedByUserUseCase = get(),
    addProductToUserUseCase: AddProductToUserUseCase = get(),
    productRepository: ProductRepository = get(),
) {
    val basePath = "/products"
    crudRoute(
        basePath = basePath,
        repository = productRepository,
    )
    route(basePath) {
        get("") {
            call.withValidQueryParameter<String>(
                paramName = "tagId",
            ) { tagId ->
                call.safeRespond {
                    val productByTagId = getProductByTagIdUseCase(tagId = tagId)
                    call.respond(HttpStatusCode.OK, productByTagId)
                }
            }
        }


        get("") {
            call.withValidQueryParameter<Int>(
                paramName = "ownerUserId",
            ) { ownerUserId ->
                call.safeRespond {
                    val productsByownerUserId = getProductsOwnedByUserUseCase(ownerUserId = ownerUserId)
                    call.respond(HttpStatusCode.OK, productsByownerUserId)
                }
            }
        }

        post("/{userId}/products") {
            call.withValidParameter(
                paramName = "userId",
                parser = String::toIntOrNull
            ) { userId ->
                call.withValidBody<AddProductToUserRequest> { body ->
                    call.safeRespond {
                        val productId = body.productId
                        addProductToUserUseCase(productId = productId, userId = userId)
                        call.respond(
                            HttpStatusCode.Created,
                            "Product with id = $productId added to user with id = $userId."
                        )
                    }
                }
            }
        }
    }
}