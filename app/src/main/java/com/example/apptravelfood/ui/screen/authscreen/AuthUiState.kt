package com.example.apptravelfood.ui.screen.authscreen

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",

    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val loggedUserId: Long? = null,
    val error: String? = null,
    val registerSuccess: Boolean = false,
    val forgotPasswordMode: Boolean = false,
    val otpSent: Boolean = false,
    val otp: String = "",
    val newPassword: String = "",
)