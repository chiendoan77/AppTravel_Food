package com.example.apptravelfood.data.repository

import com.example.apptravelfood.data.remote.api.AuthApi
import com.example.apptravelfood.data.remote.dto.ResetPasswordRequest
import com.example.apptravelfood.data.remote.dto.SendOtpRequest

class OtpRepository(
    private val authApi: AuthApi
) {

    suspend fun sendOtp(email: String) =
        authApi.sendOtp(
            SendOtpRequest(email)
        )

    suspend fun resetPassword(
        email: String,
        otp: String,
        newPasswordHash: String
    ) =
        authApi.resetPassword(
            ResetPasswordRequest(
                email = email,
                otp = otp,
                newPasswordHash = newPasswordHash
            )
        )
}