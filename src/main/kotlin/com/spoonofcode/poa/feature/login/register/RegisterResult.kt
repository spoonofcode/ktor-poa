package com.spoonofcode.poa.feature.login.register

import com.spoonofcode.poa.core.model.Register

sealed class RegisterResult {
    data class Success(val register: Register) : RegisterResult()
    object UserAlreadyExist : RegisterResult()
}