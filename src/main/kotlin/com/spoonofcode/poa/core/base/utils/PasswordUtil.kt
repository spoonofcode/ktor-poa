package com.spoonofcode.poa.core.base.utils

import org.mindrot.jbcrypt.BCrypt

class PasswordUtil {

    fun hashPassword(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt())

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean =
        BCrypt.checkpw(plainPassword, hashedPassword)
}