plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.spoonofcode.poa"
version = "0.0.1"

application {
    mainClass.set("com.spoonofcode.poa.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor client
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)

    // Ktor server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.host.common)

    // Additional JWT/auth libs
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.auth0.java.jwt)
    implementation(libs.jbcrypt)

    // Logging
    implementation(libs.logback.classic)

    // Exposed + MySQL
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.java.time)
    implementation(libs.mysql.connector.java)

    // Koin + Hikari + Google
    implementation(libs.koin.ktor)
    implementation(libs.hikaricp)
    implementation(libs.google.api.client)

    // Firebase
    implementation(libs.firebase.admin)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
