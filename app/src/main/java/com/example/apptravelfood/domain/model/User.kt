package com.example.apptravelfood.domain.model

data class User(
    val userId: Long,
    val fullName: String,
    val email: String,
    val phone: String?,
    val avatarUrl: String?,
    val totalPoint: Int,
    val role: String
)