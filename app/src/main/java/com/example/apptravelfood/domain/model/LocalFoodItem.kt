package com.example.apptravelfood.domain.model

data class LocalFoodItem(
    val name: String,
    val address: String,
    val rating: Double?,
    val imageUrl: String? = null
)