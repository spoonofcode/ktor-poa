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
    val cfg = environment.config
    val jdbcUrl = cfg.propertyOrNull("storage.jdbcUrl")?.getString()
    val driverClassName = cfg.propertyOrNull("storage.driverClassName")?.getString()
    val user = cfg.propertyOrNull("storage.user")?.getString()
    val password = cfg.propertyOrNull("storage.password")?.getString()
    val maxPoolSize = cfg.propertyOrNull("storage.maximumPoolSize")?.getString()?.toIntOrNull() ?: 5

    require(!jdbcUrl.isNullOrBlank()) { "JDBC_URL missing" }

    val database = Database.connect(
        provideDataSource(
            url = jdbcUrl,
            driverClass = driverClassName,
            user = user,
            pass = password,
            maximumPool = maxPoolSize,
        )
    )
    transaction(database) {
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
            UserPartners
        )
        setExampleData()
    }
}

private fun provideDataSource(
    url: String,
    driverClass: String?,
    user: String?,
    pass: String?,
    maximumPool: Int,
): HikariDataSource {
    val hikariConfig = HikariConfig().apply {
        jdbcUrl = url
        driverClassName = driverClass
        username = user
        password = pass
        maximumPoolSize = maximumPool
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
            UserPartners
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
        it[nickName] = "Lycha 5"
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
    createProductsWithTagWithoutUser()

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
    UserPartners.insert {
        it[user] = 2
        it[partner] = 1
    }

    UserPartners.insert {
        it[user] = 2
        it[partner] = 2
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
    repeat(10) { index ->

        val productSeriesId = (1..getImageLinks().size).random()
        val productUserId = getOwnerUserIdOrNull()

        Products.insert {
            it[name] = "Product #${index + 1}"
            it[description] = "Przykładowy opis wydarzenia nr ${index + 1}"
            it[seriesId] = productSeriesId.toString()
            it[collectionName] = "Collection 1"
            it[imageLink] = getImageLinks()[productSeriesId - 1]
            it[websiteLink] = "https://beautysaute.pl/"
            it[customLink] = "https://beautysaute.pl/"
            it[ownerUserId] = productUserId
        }
    }
}

fun createProductsWithTagWithoutUser()  {
    Products.insert {
        it[name] = "Product 997"
        it[description] = "First unique T-shirt collection by Proof of Wear!"
        it[tagId] = "043469C2891D91" //TAG from the pendant
        it[seriesId] = "997"
        it[collectionName] = "Collection 1"
        it[imageLink] = "https://i2.seadn.io/polygon/0x96bed0ae3ae5b0f8a2b92a9315981af25080cf9f/1f2918b2f64905471b373fdb21b400/571f2918b2f64905471b373fdb21b400.png"
        it[videoLink] = "https://raw2.seadn.io/polygon/0x96bed0ae3ae5b0f8a2b92a9315981af25080cf9f/2683651f9f497b89f4209445939631/8c2683651f9f497b89f4209445939631.mp4"
        it[websiteLink] = "https://proof-of-wear.com/"
        it[customLink] = "https://proof-of-wear.com/"
    }

    Products.insert {
        it[name] = "Product 998"
        it[description] = "First unique T-shirt collection by Proof of Wear!"
        it[tagId] = "047AECC2891D90" //TAG from the pendant
        it[seriesId] = "998"
        it[collectionName] = "Collection 1"
        it[imageLink] = "https://i2.seadn.io/polygon/0x052f027da88d0a6a76515346615206d2457efc45/8728bc26abed65e90e83704c77d3e9/618728bc26abed65e90e83704c77d3e9.jpeg"
        it[videoLink] = "https://raw2.seadn.io/polygon/0x052f027da88d0a6a76515346615206d2457efc45/7d5369795b7750eb22c0841db4d29f/2c7d5369795b7750eb22c0841db4d29f.mp4"
        it[websiteLink] = "https://proof-of-wear.com/"
        it[customLink] = "https://proof-of-wear.com/"
    }

    Products.insert {
        it[name] = "Product 999"
        it[description] = "Przykładowy opis wydarzenia nr 999"
        it[tagId] = "04A971E2E51090" //TAG from the "Money makes money" hat
        it[seriesId] = "999"
        it[collectionName] = "Collection 1"
        it[imageLink] = "https://i2.seadn.io/polygon/0xc8b0f59f636b72752578a86c0337f1a4f675a40d/a256c49770bbf0ec4458d9ddd35274/e0a256c49770bbf0ec4458d9ddd35274.png"
        it[videoLink] = "https://raw2.seadn.io/polygon/0xc8b0f59f636b72752578a86c0337f1a4f675a40d/6bbd5b933cec2f1321e49bf9b7d950/de6bbd5b933cec2f1321e49bf9b7d950.mp4"
        it[websiteLink] = "https://proof-of-wear.com/"
        it[customLink] = "https://proof-of-wear.com/"
    }
}

// random: 70% chance of userId, 30% chance of null
private fun getOwnerUserIdOrNull() = if ((1..10).random() <= 7) (1..4).random() else null

private fun getImageLinks(): List<String> = listOf(
    "https://beautysaute.pl/environment/cache/images/750_750_productGfx_261/bluza-damska-z-kapturem-ocieplana-bordo.webp",
    "https://beautysaute.pl/environment/cache/images/750_750_productGfx_267/komplet-mocca-post.webp",
    "https://beautysaute.pl/environment/cache/images/750_750_productGfx_259/Bluza-z-kapturem-damska-czarna-ocieplana.webp",
    "https://beautysaute.pl/environment/cache/images/750_750_productGfx_257/bluza-damska-z-kapturem-pudrowy-roz.webp",
    "https://beautysaute.pl/environment/cache/images/750_750_productGfx_262/Bluza-damska-bez-kaptura-czekolada.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_275/wygodny-damski-komplet-dresowy-bez.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_246/Damska-bluza-bez-kaptura-bezowa.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_258/Bluza-damska-bez-kaptura-czarna.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_249/Modna-bluza-damska-bez-kaptura-roz.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_256/bluza-damska-oversize-bezowa.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_260/Bluza-damska-z-kapturem-butelkow-zielen.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_268/bluza-z-kapturem-czekoladowa-damska.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_250/Ocieplne-spodnie-dresowe-damskie-butelkowa-zielen.webp",
    "https://beautysaute.pl/environment/cache/images/0_0_productGfx_255/Spodnie-dresowe-damskie-czarne-ocieplane.webp",
)

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }