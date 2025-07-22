package com.spoonofcode.poa.core.network

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import java.util.*

object JwtConfig {
    const val CLAIM_EMAIL = "email"
    const val CLAIM_REFRESH = "refresh"

    private const val SECRET = "your-secret-key"
    private const val ISSUER = "ktor-poa-jwt-issuer"
    private const val AUDIENCE = "ktor-poa-jwt-audience"
    private const val REALM = "ktor-poa-jwt-realm"

    // 15 minutes for access token (in milliseconds)
    private const val ACCESS_TOKEN_VALIDITY_IN_MILLI_SECONDS = 15 * 60 * 1000L

    // 7 days for refresh token (in milliseconds)
    private const val REFRESH_TOKEN_VALIDITY_IN_MILLI_SECONDS = 7 * 24 * 60 * 60 * 1000L

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun configureKtorFeature(config: JWTAuthenticationProvider.Config) {
        with(config) {
            verifier(getVerifier())
            realm = REALM
            validate { credential -> validateCredential(credential) }
        }
    }

    fun createAccessToken(userId: String, email: String): String =
        createToken(userId, email, ACCESS_TOKEN_VALIDITY_IN_MILLI_SECONDS)

    fun createRefreshToken(userId: String, email: String): String =
        createToken(userId, email, REFRESH_TOKEN_VALIDITY_IN_MILLI_SECONDS, isRefresh = true)

    fun getVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun validateCredential(credential: JWTCredential): JWTPrincipal? {
        return if (
            with(credential.payload) {
                audience.contains(AUDIENCE) &&
                        issuer.contains(ISSUER) &&
                        subject.isNullOrBlank().not() &&
                        getClaim(CLAIM_EMAIL).asString().isNullOrEmpty().not()
            }


        ) {
            JWTPrincipal(credential.payload)
        } else null
    }

    private fun createToken(
        userId: String,
        email: String,
        validityInMs: Long,
        isRefresh: Boolean = false
    ): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withSubject(userId)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim(CLAIM_EMAIL, email)
            .apply {
                if (isRefresh) {
                    withClaim(CLAIM_REFRESH, true)
                }
            }
            .withExpiresAt(Date(now + validityInMs))
            .sign(algorithm)
    }
}