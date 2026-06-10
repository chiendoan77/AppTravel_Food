package com.example.apptravelfood.data.remote.api

import com.example.apptravelfood.data.remote.dto.ApiMessageResponse
import com.example.apptravelfood.data.remote.dto.ResetPasswordRequest
import com.example.apptravelfood.data.remote.dto.SendOtpRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/send-otp.php")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): ApiMessageResponse

    @POST("auth/reset-password.php")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ApiMessageResponse
}