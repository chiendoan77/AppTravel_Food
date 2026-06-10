package com.example.apptravelfood.core.untils

import java.security.MessageDigest

object PasswordUtils {

    private const val SALT = "TravelFood_2026"

    fun hash(password: String): String {
        val input = password + SALT

        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }
}