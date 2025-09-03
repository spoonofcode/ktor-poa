package com.spoonofcode.poa.core.base.routes

import com.spoonofcode.poa.core.base.repository.CrudRepository
import com.spoonofcode.poa.core.network.ext.safeRespond
import com.spoonofcode.poa.core.network.ext.withValidParameter
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

enum class CrudOperation { Create, Read, Update, Delete, All }

internal inline fun <reified RQ : Any, reified RS : Any> Route.crudRoute(
    basePath: String,
    repository: CrudRepository<RQ, RS>,
    vararg crudOperations: CrudOperation = CrudOperation.entries.toTypedArray()
) {
    route(basePath) {
        if (CrudOperation.Create in crudOperations) {
            post("/") {
                call.safeRespond {
                    val newItem = call.receive(RQ::class)
                    val createdItem = repository.create(newItem)
                    call.respond(HttpStatusCode.Created, createdItem)
                }
            }
        }

        if (CrudOperation.Read in crudOperations) {
            get("/{id}") {
                call.withValidParameter(
                    paramName = "id",
                    parser = String::toIntOrNull
                ) { itemId ->
                    call.safeRespond {
                        val item = repository.read(itemId)
                        if (item != null) {
                            call.respond(HttpStatusCode.OK, item)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Item with id = $itemId not found.")
                        }
                    }
                }
            }
        }

        if (CrudOperation.Update in crudOperations) {
            put("/{id}") {
                call.withValidParameter(
                    paramName = "id",
                    parser = String::toIntOrNull
                ) { itemId ->
                    call.safeRespond {
                        val updatedItem = call.receive(RQ::class)
                        val itemUpdated = repository.update(itemId, updatedItem)
                        if (itemUpdated) {
                            call.respond(HttpStatusCode.OK, "Item with id = $itemId has been updated.")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Item with id = $itemId not found.")
                        }
                    }
                }
            }
        }

        if (CrudOperation.Delete in crudOperations) {
            delete("/{id}") {
                call.withValidParameter(
                    paramName = "id",
                    parser = String::toIntOrNull
                ) { itemId ->
                    call.safeRespond {
                        val itemDeleted = repository.delete(itemId)
                        if (itemDeleted) {
                            call.respond(HttpStatusCode.OK, "Item with id = $itemId deleted.")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Item with id = $itemId not found.")
                        }
                    }
                }
            }
        }

        if (CrudOperation.All in crudOperations) {
            get("/") {
                call.safeRespond {
                    val items = repository.readAll()
                    call.respond(HttpStatusCode.OK, items)
                }
            }
        }
    }
}