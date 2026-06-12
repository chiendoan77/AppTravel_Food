package com.example.apptravelfood.data.remote.dto

data class AppNotificationDto(
    val notificationId: String = "",
    val receiverUserId: Long = 0,
    val senderUserId: Long = 0,
    val senderName: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "REVIEW",
    val foodStoreId: Long = 0,
    val reviewId: Long = 0,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)