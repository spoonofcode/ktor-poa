package com.spoonofcode.poa.core.network.ext

import com.spoonofcode.poa.core.network.ParamSpec
import com.spoonofcode.poa.core.network.ParamValues
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend fun ApplicationCall.safeRespond(block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        // TODO Change later with general error message or specify mapping for exceptions with messages
//        respond(HttpStatusCode.InternalServerError, "An error occurred while processing your request.")
        respond(HttpStatusCode.InternalServerError, e.message ?: e.stackTraceToString())
    }
}

suspend fun <T> ApplicationCall.withValidParameter(
    paramName: String,
    parser: (String) -> T?,
    block: suspend (T) -> Unit,
) {
    val parsedValue = parameters[paramName]
        ?.let { parser(it) }
        ?: return respond(HttpStatusCode.BadRequest, "Missing or invalid '$paramName' parameter.")

    block(parsedValue)
}

suspend fun ApplicationCall.validParameters(
    vararg specs: ParamSpec<*>
): ParamValues? {
    val out = LinkedHashMap<String, Any>(specs.size)
    for (specAny in specs) {
        @Suppress("UNCHECKED_CAST")
        val spec = specAny as ParamSpec<Any>
        val raw = parameters[spec.name]
            ?: return respond(HttpStatusCode.BadRequest, "Missing '${spec.name}' parameter.").let { null }
        val parsed = spec.parser(raw)
            ?: return respond(HttpStatusCode.BadRequest, "Invalid '${spec.name}' parameter: '$raw'.").let { null }
        out[spec.name] = parsed
    }
    return ParamValues(out)
}

suspend fun ApplicationCall.withValidParameters(
    vararg specs: ParamSpec<*>,
    block: suspend (ParamValues) -> Unit
) {
    val values = validParameters(*specs) ?: return
    block(values)
}

suspend inline fun <reified T> ApplicationCall.withValidQueryParameter(
    paramName: String,
    crossinline block: suspend (T) -> Unit
) {
    val rawValue = request.queryParameters[paramName]
        ?: return respond(HttpStatusCode.BadRequest, "Missing '$paramName' parameter.")

    val parsedValue = tryConvert<T>(rawValue)
        ?: return respond(HttpStatusCode.BadRequest, "Missing or invalid '$paramName' parameter.")

    block(parsedValue)
}

suspend inline fun <reified T : Any> ApplicationCall.withValidBody(block: suspend (T) -> Unit) {
    val body = receiveNullable<T>()
    if (body == null) {
        respond(HttpStatusCode.BadRequest, "Invalid body.")
    } else {
        block(body)
    }
}

inline fun <reified T> tryConvert(value: String): T? {
    return when {
        // Handle enums via reflection:
        T::class.java.isEnum -> {
            val enumConstants = T::class.java.enumConstants as Array<Enum<*>>
            val match = enumConstants.firstOrNull { it.name.equals(value, ignoreCase = true) }
            match as? T
        }

        // Built-in conversions:
        T::class == String::class  -> value as T
        T::class == Int::class     -> value.toIntOrNull() as T?
        T::class == Long::class    -> value.toLongOrNull() as T?
        T::class == Boolean::class -> value.toBooleanStrictOrNull() as T?
        T::class == Float::class   -> value.toFloatOrNull() as T?
        T::class == Double::class  -> value.toDoubleOrNull() as T?

        else -> null  // or throw an exception, depending on your needs
    }
}

