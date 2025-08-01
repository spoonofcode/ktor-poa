package com.spoonofcode.poa.feature.login.login

import com.spoonofcode.poa.core.model.LoginGoogle

sealed class LoginGoogleResult {
    data class Success(val loginGoogle: LoginGoogle) : LoginGoogleResult()
    object InvalidCredentials : LoginGoogleResult()
}