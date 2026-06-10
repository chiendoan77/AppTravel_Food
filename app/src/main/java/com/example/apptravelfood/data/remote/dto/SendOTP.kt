package com.example.apptravelfood.data.remote.dto

data class SendOtpRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPasswordHash: String
)

data class ApiMessageResponse(
    val success: Boolean,
    val message: String
)