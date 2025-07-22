package com.spoonofcode.poa.feature.login.register

import com.spoonofcode.poa.core.model.RegisterResponse

sealed class RegisterResult {
    data class Success(val registerResponse: RegisterResponse) : RegisterResult()
    object UserAlreadyExist : RegisterResult()
}