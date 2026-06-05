package com.example.apptravelfood.domain.model

data class FoodStoreReview(
    val reviewId: Long,
    val foodStoreId: Long,
    val userId: Long,
    val rating: Float,
    val comment: String,
    val createdAt: Long
)