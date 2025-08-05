package com.spoonofcode.poa.core.domain.login

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.apache.v2.ApacheHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.spoonofcode.poa.core.data.repository.UserRepository
import com.spoonofcode.poa.core.model.LoginGoogle
import com.spoonofcode.poa.core.model.UserRequest
import com.spoonofcode.poa.core.network.JwtConfig
import com.spoonofcode.poa.feature.login.login.LoginGoogleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginGoogleUseCase(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(googleUserToken: String): LoginGoogleResult {
        // TODO Consider injecting these two
        val transport = ApacheHttpTransport()
        val factory = GsonFactory.getDefaultInstance()

        val verifier = GoogleIdTokenVerifier.Builder(transport, factory)
            .setAudience(listOf(System.getenv("POA_GOOGLE_CLIENT_ID"))) // Server client id from google cloud console
            .build()

        // Verify received token
        val googleIdToken = withContext(Dispatchers.IO) {
            verifier.verify(googleUserToken)
        }

        if (googleIdToken != null) {
            val payload = googleIdToken.payload
            val email = payload.email

            // verify email
            val existingUser = userRepository.readByEmail(email)
            if (existingUser != null) {
                val jwtAccessToken = JwtConfig.createAccessToken(
                    userId = existingUser.id.toString(),
                    email = existingUser.email,
                )
                val jwtRefreshToken = JwtConfig.createRefreshToken(
                    userId = existingUser.id.toString(),
                    email = existingUser.email,
                )

                return LoginGoogleResult.Success(
                    LoginGoogle(
                        userId = existingUser.id,
                        jwtAccessToken = jwtAccessToken,
                        jwtRefreshToken = jwtRefreshToken,
                    )
                )
            } else {
                val firstName = payload["given_name"]?.toString() ?: email.substringBefore("@")
                val lastName = payload["family_name"]?.toString() ?: email.substringBefore("@")
                payload["picture"]?.toString()

                val newUser = userRepository.create(
                    UserRequest(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        provider = "google",
                        providerId = googleIdToken.payload.userId
                    )
                )

                val jwtAccessToken = JwtConfig.createAccessToken(
                    userId = newUser.id.toString(),
                    email = newUser.email,
                )
                val jwtRefreshToken = JwtConfig.createRefreshToken(
                    userId = newUser.id.toString(),
                    email = newUser.email,
                )

                return LoginGoogleResult.Success(
                    LoginGoogle(
                        userId = newUser.id,
                        jwtAccessToken = jwtAccessToken,
                        jwtRefreshToken = jwtRefreshToken,
                    )
                )

            }
        }
        return LoginGoogleResult.InvalidCredentials
    }
}