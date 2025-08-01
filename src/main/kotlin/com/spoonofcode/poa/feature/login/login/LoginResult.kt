package com.spoonofcode.poa.feature.login.login

import com.spoonofcode.poa.core.model.Login

sealed class LoginResult {
    data class Success(val login: Login) : LoginResult()
    object InvalidCredentials : LoginResult()
    object UserNotFound : LoginResult()
}