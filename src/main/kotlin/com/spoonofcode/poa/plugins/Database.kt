package com.spoonofcode.poa.plugins

import com.spoonofcode.poa.core.model.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val driverClass = environment.config.property("storage.driverClassName").getString()
    val jdbcUrl = environment.config.property("storage.jdbcURL").getString()
    val db = Database.connect(provideDataSource(jdbcUrl, driverClass))
    transaction(db) {
        dropTables()
        SchemaUtils.create(
            Users,
            Roles,
            UserRoles,
            Products,
            UserProducts,
            Partners,
        )
        setExampleData()
    }
}

private fun provideDataSource(url: String, driverClass: String): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = driverClass
        jdbcUrl = url
        maximumPoolSize = 3
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }
    return HikariDataSource(hikariConfig)
}

private fun dropTables() {
    transaction {
        SchemaUtils.drop(
            Users,
            Roles,
            UserRoles,
            Products,
            UserProducts,
            Partners,
        )
    }
}

private fun setExampleData() {
    Roles.insert { it[name] = "ADMIN" }
    Roles.insert { it[name] = "CLUB_OWNER" }
    Roles.insert { it[name] = "COACH" }
    Roles.insert { it[name] = "USER" }

    Users.insert {
        it[firstName] = "Bartosz"
        it[lastName] = "Łuczak"
        it[nickName] = "Lycha"
        it[email] = "luczak.bartosz5@gmail.com"
        it[provider] = "google"
        it[providerId] = "117628026316806676295"
    }

    UserRoles.insert {
        it[userId] = 1
        it[roleId] = 1
    }

    Users.insert {
        it[firstName] = "Bartosz"
        it[lastName] = "Luczak"
        it[nickName] = "Lycha"
        it[email] = "bartosz.luczak@gmail.com"
        it[password] = "\$2a\$10\$JvONt8faWClBF4Y5D.9uQO8x2DJDDiVw8VcRwBWmB94tP67WQKNtK"
    }

    UserRoles.insert {
        it[userId] = 2
        it[roleId] = 1
    }

    Users.insert {
        it[firstName] = "Michal"
        it[lastName] = "Staroszczyk"
        it[email] = "michal.staroszczyk@gmail.com"
        it[password] = "\$2a\$10\$zCx5qtCaWxzP/L6hB3pH6u0wMhvev3WAqokQQ8UcmnYDOI6bNEjS."
    }

    UserRoles.insert {
        it[userId] = 3
        it[roleId] = 2
    }

    Users.insert {
        it[firstName] = "Daria"
        it[lastName] = "Waszkiewicz"
        it[nickName] = "DariaWasz"
        it[email] = "daria.waszkiewicz@gmail.com"
        it[password] = "\$2a\$10\$0AgsnrhIbbq3e0jWeW.g0.kniIrjjCXWAs81y69hymh.04YJTKmC."
    }

    UserRoles.insert {
        it[userId] = 4
        it[roleId] = 3
    }

    seedProducts()

    UserProducts.insert {
        it[userId] = 1
        it[productId] = 1
    }

    UserProducts.insert {
        it[userId] = 1
        it[productId] = 2
    }

    Partners.insert {
        it[name] = "Beauty Saute"
        it[email] = "beauty.saute@gmail.com"
        it[description] = "Beauty Sautè to więcej niż odzież – to filozofia."
        it[imageLink] = "https://beautysaute.pl/environment/cache/images/0_0_storefrontImages_11a16d30-d85a-422f-917d-195c30274147.png"
        it[websiteLink] = "https://beautysaute.pl/"
        it[instagramLink] = "https://www.instagram.com/beauty_saute_fashion/?igsh=Y3FxN2U3bDRya2F0&utm_source=qr#"
        it[facebookLink] = "https://www.facebook.com/profile.php?id=61568636916637"
        it[youtubeLink] = "https://www.instagram.com/beauty_saute_fashion/?igsh=Y3FxN2U3bDRya2F0&utm_source=qr#"
        it[xLink] = "https://www.instagram.com/beauty_saute_fashion/?igsh=Y3FxN2U3bDRya2F0&utm_source=qr#"
    }

    Partners.insert {
        it[name] = "Proof of Wear"
        it[email] = "proof.of.wear@gmail.com"
        it[description] = "Proof of Wear – sprawdz to."
        it[imageLink] = "https://proof-of-wear.com/images/logo.jpg"
        it[websiteLink] = "https://proof-of-wear.com/"
        it[instagramLink] = "https://www.instagram.com/proof_of_wear/?igsh=MXMwODNiZW1qNTRlMA%3D%3D#"
        it[facebookLink] = "https://www.instagram.com/proof_of_wear/?igsh=MXMwODNiZW1qNTRlMA%3D%3D#"
        it[youtubeLink] = "https://www.instagram.com/proof_of_wear/?igsh=MXMwODNiZW1qNTRlMA%3D%3D#"
        it[xLink] = "https://x.com/ProofOfWearPOW?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor"
    }
}

fun seedProducts() {
    repeat(200) { index ->
        Products.insert {
            it[name] = "Product #${index + 1}"
            it[description] = "Przykładowy opis wydarzenia nr ${index + 1}"
            it[tagId] = "123456789"
            it[collectionName] = "Collection 1"
            it[websiteLink] = "https://beautysaute.pl/"
            it[customLink] = "https://beautysaute.pl/"
            it[ownerUserId] = (1..4).random()
        }
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }