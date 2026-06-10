package com.example.apptravelfood.core.untils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return email.length > 5 && email.endsWith("@gmail.com")
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^0\\d{9}$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 5 &&
                password.any { it.isUpperCase() }
    }
}