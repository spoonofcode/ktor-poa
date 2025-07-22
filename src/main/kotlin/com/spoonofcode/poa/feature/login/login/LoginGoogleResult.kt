package com.spoonofcode.poa.feature.login.login

import com.spoonofcode.poa.core.model.LoginGoogleResponse

sealed class LoginGoogleResult {
    data class Success(val loginGoogleResponse: LoginGoogleResponse) : LoginGoogleResult()
    object InvalidCredentials : LoginGoogleResult()
}