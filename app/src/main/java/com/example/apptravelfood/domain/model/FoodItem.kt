package com.example.apptravelfood.domain.model

data class FoodItem(
    val foodItemId: Long,
    val foodStoreId: Long,
    val name: String,
    val description: String?,
    val price: Double?,
    val imageUrl: String?
)