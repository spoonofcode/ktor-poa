package com.spoonofcode.poa.core.network

class ParamSpec<T : Any>(
    val name: String,
    val parser: (String) -> T?
)

fun <T : Any> p(name: String, parser: (String) -> T?) = ParamSpec(name, parser)


class ParamValues internal constructor(
    private val values: Map<String, Any>
) {
    @Suppress("UNCHECKED_CAST")
    internal inline operator fun <reified T : Any> get(name: String): T =
        values[name] as T
    fun asMap(): Map<String, Any> = values
}

// ---- Handy spec helpers ----
fun pInt(name: String) = p(name, String::toIntOrNull)
fun pLong(name: String) = p(name, String::toLongOrNull)
fun pBoolStrict(name: String) = p(name, String::toBooleanStrictOrNull)
fun pNonBlank(name: String) = p(name) { s -> s.takeIf { it.isNotBlank() } }
fun pString(name: String) = p(name) { it } // accepts empty string