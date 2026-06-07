package com.example.apptravelfood.ui.screen.food.foodstoredetailscreen

import com.example.apptravelfood.data.local.entity.FoodItemEntity
import com.example.apptravelfood.data.local.entity.FoodStoreEntity
import com.example.apptravelfood.data.local.entity.FoodStoreReviewEntity

data class FoodStoreDetailUiState(
    val isLoading: Boolean = false,

    val store: FoodStoreEntity? = null,
    val foodItems: List<FoodItemEntity> = emptyList(),
    val reviews: List<FoodStoreReviewEntity> = emptyList(),

    val myReview: FoodStoreReviewEntity? = null,

    val averageRating: Float = 0f,
    val reviewCount: Int = 0,

    val isOwner: Boolean = false,

    val ratingInput: Float = 5f,
    val commentInput: String = "",

    val message: String? = null,
    val error: String? = null
)