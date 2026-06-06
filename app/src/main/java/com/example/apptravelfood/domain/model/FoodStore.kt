package com.example.apptravelfood.domain.model

data class FoodStore(
    val foodStoreId: Long,
    val placeId: String,
    val createdByUserId: Long,
    val name: String,
    val address: String?,
    val imageUrl: String?,
    val description: String?,

    val averageRating: Float = 0f,
    val reviewCount: Int = 0
)