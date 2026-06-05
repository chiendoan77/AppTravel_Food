package com.example.apptravelfood.domain.model

data class PointHistory(
    val pointHistoryId: Long,
    val userId: Long,
    val point: Int,
    val type: String,
    val description: String?,
    val createdAt: Long
)