package com.example.apptravelfood.domain.model

data class Checkin(
    val checkinId: Long,
    val userId: Long,
    val imageUrl: String?,
    val pointEarned: Int,
    val faceVerified: Boolean,
    val checkinTime: Long
)