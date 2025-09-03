package com.spoonofcode.poa.feature.product

import com.spoonofcode.poa.core.base.routes.CrudOperation
import com.spoonofcode.poa.core.base.routes.crudRoute
import com.spoonofcode.poa.core.data.repository.ProductRepository
import com.spoonofcode.poa.core.domain.product.GetProductByTagIdUseCase
import com.spoonofcode.poa.core.network.ext.safeRespond
import com.spoonofcode.poa.core.network.ext.withValidQueryParameter
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Route.products(
    getProductByTagIdUseCase: GetProductByTagIdUseCase = get(),
    productRepository: ProductRepository = get(),
) {
    val basePath = "/products"
    crudRoute(
        basePath = basePath,
        repository = productRepository,
        CrudOperation.Read, CrudOperation.Update, CrudOperation.All,
    )
    route(basePath) {
        param("tagId") {
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
        }
    }
}