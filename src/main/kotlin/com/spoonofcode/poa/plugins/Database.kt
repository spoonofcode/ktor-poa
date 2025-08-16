package com.spoonofcode.poa.plugins

import com.spoonofcode.poa.core.model.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

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
            PartnerCategories,
            Partners,
            Notifications,
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
            PartnerCategories,
            Partners,
            Notifications,
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

    createProducts()

    UserProducts.insert {
        it[userId] = 1
        it[productId] = 1
    }

    UserProducts.insert {
        it[userId] = 1
        it[productId] = 2
    }

    createPartnerCategories()

    createPartners()

    createNotifications()
}

private fun createPartnerCategories() {
    PartnerCategories.insert {
        it[name] = "Beuty"
    }

    PartnerCategories.insert {
        it[name] = "Sport"
    }

    PartnerCategories.insert {
        it[name] = "Clothes"
    }

    PartnerCategories.insert {
        it[name] = "Music"
    }

    PartnerCategories.insert {
        it[name] = "Technology"
    }
}

private fun createPartners() {
    Partners.insert {
        it[name] = "Beauty Saute"
        it[email] = "beauty.saute@gmail.com"
        it[description] = "Beauty Sautè to więcej niż odzież – to filozofia."
        it[imageLink] =
            "https://beautysaute.pl/environment/cache/images/0_0_storefrontImages_11a16d30-d85a-422f-917d-195c30274147.png"
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

private fun createNotifications() {
    Notifications.insert {
        it[title] = "\uD83D\uDE80 Rocket Sale – Don't Miss Out! \uD83D\uDE80"
        it[text] =
            "Blast off into savings with our exclusive Rocket Sale! \uD83D\uDCE6\ufe0f Get up to 50% OFF on select items. Limited time offer! ⏰"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83C\uDF7B Sweet Deals – Now Available! \uD83C\uDF7B"
        it[text] =
            "Indulge in our sweet deals! \uD83C\uDF6D Enjoy discounts up to 40% on selected treats. Hurry, they won't last long! ⏳"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83D\uDC8E Jewelry Discount – Sparkle and Shine! \uD83D\uDC8E"
        it[text] =
            "Shine bright with our latest jewelry collection! \uD83D\uDC8A Get 25% OFF on all accessories. Limited stock, grab yours now! 💍"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83D\uDC4C️ Summer Fashion is Here! \uD83D\uDC4C️"
        it[text] =
            "Upgrade your wardrobe with fresh summer styles! \uD83C\uDF34 Now with 30% OFF on select items! Get ready for the sun! 🌞"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83D\uDC9B Cozy Winter Styles – Shop Now! \uD83D\uDC9B"
        it[text] =
            "Stay warm this winter with our cozy collection! ❄️ Enjoy up to 40% OFF on selected winter wear. Don’t miss out on these savings! 🧣"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83D\uDC8B Mega Tech Sale – Limited Time! \uD83D\uDC8B"
        it[text] =
            "Gear up for the ultimate tech sale! 📱 Get up to 60% OFF on selected gadgets. Shop now and save big on cutting-edge tech! 💻"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83C\uDF89 Party Time – Special Offers! \uD83C\uDF89"
        it[text] =
            "Celebrate in style with our Party Time collection! 🎉 Save up to 35% on party essentials. Get the best deals before they're gone! 🍾"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83C\uDF73 Spring Collection Launch! \uD83C\uDF73"
        it[text] =
            "Freshen up your wardrobe with our new Spring Collection! 🌸 Enjoy up to 30% OFF on all items. Spring into style today! 🌼"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83C\uDF51 Big Game Sale – Save Now! \uD83C\uDF51"
        it[text] =
            "Get ready for the big game with exclusive deals! 🏈 Score up to 50% OFF on select merchandise. Don’t wait – shop now! 🏆"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
    Notifications.insert {
        it[title] = "\uD83D\uDD25 Hot Sale – Fire Deals! \uD83D\uDD25"
        it[text] =
            "Our Hot Sale is live! 🔥 Get 30% OFF on everything. Shop now for sizzling discounts on our bestsellers! 🛍️"
        it[link] = "https://beautysaute.pl/pl/p/Bluza-z-kapturem-Saute-mocca/105"
        it[expirationDate] = getExpirationDateTime()
    }
}


private fun getExpirationDateTime(): LocalDateTime {
    val now = Clock.System.now()
    val expirationDateTime = (now + (1..30).random().days + (1..24).random().hours).toLocalDateTime(TimeZone.UTC)
    return expirationDateTime
}

fun createProducts() {
    repeat(200) { index ->
        Products.insert {
            it[name] = "Product #${index + 1}"
            it[description] = "Przykładowy opis wydarzenia nr ${index + 1}"
            it[tagId] = (1..5000).random().toString()
            it[seriesId] = (1..1).random().toString()
            it[collectionName] = "Collection 1"
            it[websiteLink] = "https://beautysaute.pl/"
            it[customLink] = "https://beautysaute.pl/"
            it[ownerUserId] = (1..4).random()
        }
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }