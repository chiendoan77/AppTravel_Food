package com.example.apptravelfood.core.untils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(Regex(emailRegex))
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^(0|\\+84)(\\d{9})$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 5 &&
                password.any { it.isUpperCase() }
    }
}